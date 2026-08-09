package dev.muliroz.orderhandler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OrderHandlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderHandlerApplication.class, args);
    }

}
