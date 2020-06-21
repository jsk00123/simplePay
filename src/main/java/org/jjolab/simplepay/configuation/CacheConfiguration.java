package org.jjolab.simplepay.configuation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class CacheConfiguration {
    @Bean(name = "paymentCardInfo")
    public ConcurrentHashMap<String, String> paymentCardInfo() {
        return new ConcurrentHashMap<>();
    }

    @Bean(name = "cancelPaymentInfo")
    public ConcurrentHashMap<String, String> cancelPaymentInfo() {
        return new ConcurrentHashMap<>();
    }
}
