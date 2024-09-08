package sample.cafekiosk.spring.domain.stock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StockTest {

    @DisplayName("재고의 수량이 제공된 수량보다 작은지 확인한다.")
    @Test
    void isQuantityLessThan(){
     //given
        int quantity = 2;
        Stock stock = Stock.create("001", 1);

     //when
        boolean result = stock.isQuantityLessThan(quantity);

     //then
        assertThat(result).isTrue();
    }

    @DisplayName("재고를 주어진 개수만큼 차감할 수 있다.")
    @Test
    void deductQuantity(){
     //given
        Stock stock = Stock.create("001", 1);
        int quantity = 1;

     //when
        stock.deductQuantity(quantity);

     //then
        assertThat(stock.getQuantity()).isEqualTo(0); // == isZero
    }

    @DisplayName("재고보다 많은 수량을 차감시도하는 경우 예외가 발생한다.")
    @Test
    void deductQuantity2(){
        //given
        Stock stock = Stock.create("001", 2);
        int quantity = 2;

        //when
        stock.deductQuantity(quantity);

        //then
        assertThatThrownBy(() -> stock.deductQuantity(quantity))
                .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("차감할 재고 수량이 없습니다.");
    }

//    @DisplayName("")
//    @TestFactory // Test 대신 TestFactory 사용
//    Collection<DynamicTest> dynamicTest(){ // Collection이나 Stream 사용 가능
//        return List.of(
//                DynamicTest.dynamicTest("", () -> {
//
//                }),
//                DynamicTest.dynamicTest("", () -> {
//
//                })
//        );
//    }

    @DisplayName("재고 차감 시나리오")
    @TestFactory
    Collection<DynamicTest> stockDeductionDynamicTest(){
        //given
        Stock stock = Stock.create("001", 1);

        return List.of(
                DynamicTest.dynamicTest("재고를 주어진 개수만큼 차감할 수 있다.", () -> {
                    int quantity = 1;

                    stock.deductQuantity(quantity);

                    assertThat(stock.getQuantity()).isZero();

                }),
                DynamicTest.dynamicTest("재고보다 많은 수의 수량으로 차감 시도 하는 경우 예외가 발생한다.", () -> {
                    int quantity = 1;


                    assertThatThrownBy(() -> stock.deductQuantity(quantity))
                            .isInstanceOf(IllegalArgumentException.class)
                            .hasMessage("차감할 재고 수량이 없습니다.");
                })
        );
    }
}