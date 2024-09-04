package sample.cafekiosk.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@Configuration
public class JpaAuditingConfig {
    //실제 스프링부트 테스트가 돌 때는 이 Config가 적용됨.
    //WebMvcTest의 경우는 안 돌아서 분리됨.
}