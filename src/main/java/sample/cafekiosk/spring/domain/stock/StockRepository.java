package sample.cafekiosk.spring.domain.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findAllByProductNumberIn(List<String> productNumbers); //상품번호 리스트로 재고를 조회한다.

}