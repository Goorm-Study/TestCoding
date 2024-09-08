package sample.cafekiosk.spring.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductTypeTest {

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @Test
    void containsStockType(){
     //given
        ProductType productType1 = ProductType.HANDMADE;
        ProductType productType2 = ProductType.BOTTLE;
        ProductType productType3 = ProductType.BAKERY;

     //when
        boolean result1 = ProductType.containsStockType(productType1);
        boolean result2 = ProductType.containsStockType(productType2);
        boolean result3 = ProductType.containsStockType(productType3);

     //then
        assertThat(result1).isFalse();
        assertThat(result2).isTrue();
        assertThat(result3).isTrue();
    }

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @CsvSource({"HANDMADE, false", "BOTTLE, true", "BAKERY, true"})
    @ParameterizedTest
    void containsStockType4(ProductType productType, boolean expected){
        //when
        boolean result = ProductType.containsStockType(productType);

        //then
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideProductTypesForCheckingStockType(){
        return Stream.of(
                Arguments.of(ProductType.HANDMADE, false),
                Arguments.of(ProductType.BOTTLE, true),
                Arguments.of(ProductType.BAKERY, true)
        );
    }

    @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
    @MethodSource("provideProductTypesForCheckingStockType") // 위에 소스를 명시
    @ParameterizedTest
    void containsStockType5(ProductType productType, boolean expected){
     //given

     //when
        boolean result = ProductType.containsStockType(productType);

     //then
        assertThat(result).isEqualTo(expected);
    }

}