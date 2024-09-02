package sample.cafekiosk.spring.api.service.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.service.order.request.OrderCreateRequest;
import sample.cafekiosk.spring.api.service.order.response.OrderResponse;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;
import sample.cafekiosk.spring.domain.stock.Stock;
import sample.cafekiosk.spring.domain.stock.StockRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Transactional
@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final StockRepository stockRepository;

    /*
    재고 감소는 동시성 문제의 대표적인 문제(ex. 두 사람이 동시에 재고 수를 읽고 각자 차감을 하였을 때 우선 순위를 책정할 때)
    이것의 해결 방안은 optimistic lock / pessimistic lock / ...
     */
    public OrderResponse createOrder(OrderCreateRequest request, LocalDateTime registeredDate){
        List<String> productNumbers = request.getProductNumbers();
        List<Product> products = findProductsBy(productNumbers);

        deductStockQuantities(products);


        Order order = Order.create(products, registeredDate);
        Order savedOrder = orderRepository.save(order);

        return OrderResponse.of(savedOrder);
    }

    private void deductStockQuantities(List<Product> products) {
        // 재고 차감 체크가 필요한 상품들 filter
        List<String> stockProductNumbers = extractStockProductNumbers(products);
        // 재고 엔티티 조회
        Map<String, Stock> sotckMap = createStockMapBy(stockProductNumbers);
        // 상품별 counting
        Map<String, Long> productCountingMap = createCountingMap(stockProductNumbers);
        // 재고 차감 시도
        HashSet<String> distinctStockProductNumbers = new HashSet<>(stockProductNumbers);
        for(String stockProductNumber : distinctStockProductNumbers){
            Stock stock = sotckMap.get(stockProductNumber);
            int quantity = productCountingMap.get(stockProductNumber).intValue();
            if(stock.isQuantityLessThan(quantity)){
                throw new IllegalArgumentException("재고가 부족한 상품이 있습니다.");
            }
            stock.deductQuantity(quantity);
        }
    }

    private static Map<String, Long> createCountingMap(List<String> stockProductNumbers) {
        Map<String, Long> productCountingMap = stockProductNumbers.stream()
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()));
        return productCountingMap;
    }

    private Map<String, Stock> createStockMapBy(List<String> stockProductNumbers) {
        List<Stock> stocks = stockRepository.findAllByProductNumberIn(stockProductNumbers);
        Map<String, Stock> sotckMap = stocks.stream()
                .collect(Collectors.toMap(Stock::getProductNumber, stock -> stock));
        return sotckMap;
    }

    private static List<String> extractStockProductNumbers(List<Product> products) {
        return products.stream()
                .filter(product -> ProductType.containsStockType(product.getType()))
                .map(Product::getProductNumber)
                .collect(Collectors.toList());
    }

    private List<Product> findProductsBy(List<String> productNumbers) {
        List<Product> products = productRepository.findAllByProductNumberIn(productNumbers);
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(product -> product.getProductNumber(), p -> p));

        return productNumbers.stream()
                .map(productNumber -> productMap.get(productNumber))
                .collect(Collectors.toList());
    }
}
