package sample.cafekiosk.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import sample.cafekiosk.spring.api.controller.order.OrderController;
import sample.cafekiosk.spring.api.controller.product.ProductController;
import sample.cafekiosk.spring.api.service.order.OrderService;
import sample.cafekiosk.spring.api.service.product.ProductService;

@WebMvcTest(controllers = {
        OrderController.class,
        ProductController.class
}) //컨트롤러 관련 빈들만 올릴 수 있는 가벼운 어노테이션
public abstract class ControllerTestSupport {


    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper; //직렬화&역직렬화를 도와줌

    @MockBean
    protected OrderService orderService;

    @MockBean
    protected ProductService productService; //서비스레이어에 MockBean. WebMvcTest로 컨트롤러 관련 빈만 올린 상태이므로 서비스에 MockBean처리를 해줘야됨
}
