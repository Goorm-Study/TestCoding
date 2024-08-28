package sample.cafekiosk.unit;

import lombok.Getter;
import org.hibernate.query.sqm.mutation.internal.cte.CteInsertStrategy;
import sample.cafekiosk.unit.beverage.Beverage;
import sample.cafekiosk.unit.order.Order;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CafeKiosk {

    public static final LocalTime SHOP_OPEN_TIME = LocalTime.of(10,0);
    public static final LocalTime SHOP_CLOSE_TIME = LocalTime.of(22,0);

    private final List<Beverage> beverages = new ArrayList<>();

    public void add(Beverage beverage){
        beverages.add(beverage);
    }

    public void add(Beverage beverage, int count){
        if(count <= 0){
            throw new IllegalArgumentException("count must be greater than 0");
        }

        for(int i=0; i<count; i++){
            beverages.add(beverage);
        }
    }

    public void remove(Beverage beverage){
        this.beverages.remove(beverage);
    }

    public void clear(){
        beverages.clear();
    }

    public int calculateTotalPrice() {
        int totalPrice = 0;
        for (Beverage beverage : beverages) {
            totalPrice += beverage.getPrice();
        }

        return totalPrice;
    }

    public Order createOrder(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalTime currentTime = currentDateTime.toLocalTime();
        if(currentTime.isBefore(SHOP_OPEN_TIME) || currentTime.isAfter(SHOP_CLOSE_TIME)){
            throw new IllegalArgumentException("주문 시간이 아닙니다. 관리자에게 문의하세요.");
        }

        return new Order(currentDateTime, beverages);
    }
}
