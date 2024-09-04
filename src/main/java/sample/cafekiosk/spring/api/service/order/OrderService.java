package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.controller.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    /**
     *재고 감소 -> 동시성 고민
     * optimistic lock / pessimistic lock
     */
    public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDateTime) {
        List<String> productNumbers = request.getProductNumbers();
        //Product (product상품번호로 product엔티티를 조회하는 로직이 필요
        List<Product> products = findProductsBy(productNumbers);

        deductStockQuantities(products);

        Order order = Order.create(products, registeredDateTime);
        Order savedOrder = orderRepository.save(order);

        //Order
        return OrderResponse.of(savedOrder);
    }

    private void deductStockQuantities(List<Product> products) {
        //재고 차감 체크가 필요한 제품들 filter
        List<String> stockProductNumbers = extractStockProductNumbers(products);
        //재고 엔티티 조회
        Map<String, Stock> stockMap = createStockMapBy(stockProductNumbers);
        //상품별 counting
        Map<String, Long> productCountingMap = createCountingMapBy(stockProductNumbers);

        //재고 차감 시도 : 중복 제거된 stcokProductNumbers을 돌려야
        for (String stockProductNumber : new HashSet<>(stockProductNumbers)) {
            Stock stock = stockMap.get(stockProductNumber);
            int quantity = productCountingMap.get(stockProductNumber).intValue();

            if (stock.isQuantityLessThan(quantity)) { //재고체크: 주문생성로직 수행하다가 재고에대한 차감을 시도
                throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
            }
            stock.deductQuantity(quantity); //이 안에서 재고체크 또함 왜? -> 서비스가 어떻게 돼있든 항상 올바른 재고차감 로직이 보장되어야함(단위테스트 보장)
        }
    }

    //중복을 고려해서 상품을 찾음
    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductNumber, p -> p));

        return productNumbers.stream()
                .map(productMap::get)
                .collect(Collectors.toList());
    }

    //재고 차감 체크가 필요한 상품들 filter
    private static List<String> extractStockProductNumbers(List<Product> products) {
        return products.stream()
                .filter(product -> ProductType.containsStockType(product.getType())) //타입이 재고관련 타입인지 filter
                .map(Product::getProductNumber) //Stock 필드는 Product자체가아니라 ProductNumber임임
                .collect(Collectors.toList());
    }

    //재고 엔티티 조회
    private Map<String, Stock> createStockMapBy(List<String> stockProductNumbers) {
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
        return stocks.stream()
                .collect(Collectors.toMap(Stock::getProductNumber, s -> s));
    }

    //상품별 counting
    private static Map<String, Long> createCountingMapBy(List<String> stockProductNumbers) {
        return stockProductNumbers.stream()
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));
    }
}
