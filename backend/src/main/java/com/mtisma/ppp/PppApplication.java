package com.mtisma.ppp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableGlobalMethodSecurity(
    jsr250Enabled = true
)
public class PppApplication {

    public static void main(String[] args) {
        SpringApplication.run(PppApplication.class, args);
    }
}
