package dev.muliroz.paymentjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PaymentJavaApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentJavaApplication.class, args);
    }

}
