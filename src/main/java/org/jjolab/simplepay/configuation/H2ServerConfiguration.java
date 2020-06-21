package org.jjolab.simplepay.configuation;

import org.h2.tools.Server;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

@Configuration
@Profile("local")
public class H2ServerConfiguration {
    @Bean
    @ConfigurationProperties("spring.datasource") // yml의 설정값을 Set한다.
    public com.zaxxer.hikari.HikariDataSource dataSource() throws SQLException {
        Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists").start();
        return new com.zaxxer.hikari.HikariDataSource();
    }
}
