package sample.cafekiosk.spring.domain.product;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProductType {

    HANDMADE("제조음료"), BOTTLE("병 음로"),
    BAKERY("베이커리");

    private final String text;
}
