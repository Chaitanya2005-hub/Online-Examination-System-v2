package com.stark.studentmanagement.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private static final String DEFAULT_NEON_URL = "jdbc:postgresql://ep-sparkling-bird-axklmy5h-pooler.c-4.us-east-2.aws.neon.tech/neondb?sslmode=require";
    private static final String DEFAULT_NEON_USER = "neondb_owner";
    private static final String DEFAULT_NEON_PASS = "npg_HCOPJnw1sKx2";

    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        
        String envUrl = System.getenv("SPRING_DATASOURCE_URL");
        String envUser = System.getenv("SPRING_DATASOURCE_USERNAME");
        String envPass = System.getenv("SPRING_DATASOURCE_PASSWORD");

        // If envUrl is missing, empty, or contains dummy placeholder text, fallback to Neon PostgreSQL
        if (envUrl == null || envUrl.trim().isEmpty() || envUrl.contains("<your-mysql-host>") || envUrl.contains("<") || envUrl.contains(">")) {
            properties.setUrl(DEFAULT_NEON_URL);
            properties.setUsername(DEFAULT_NEON_USER);
            properties.setPassword(DEFAULT_NEON_PASS);
            properties.setDriverClassName("org.postgresql.Driver");
        } else {
            properties.setUrl(envUrl);
            properties.setUsername(envUser != null && !envUser.trim().isEmpty() ? envUser : DEFAULT_NEON_USER);
            properties.setPassword(envPass != null && !envPass.trim().isEmpty() ? envPass : DEFAULT_NEON_PASS);
            
            if (envUrl.startsWith("jdbc:postgresql")) {
                properties.setDriverClassName("org.postgresql.Driver");
            } else if (envUrl.startsWith("jdbc:mysql")) {
                properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
            } else if (envUrl.startsWith("jdbc:h2")) {
                properties.setDriverClassName("org.h2.Driver");
            }
        }
        
        return properties;
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        
        // Configure robust HikariCP settings for serverless cloud connection
        dataSource.setConnectionTimeout(30000); // 30 seconds for cold-start serverless compute
        dataSource.setIdleTimeout(300000); // 5 minutes
        dataSource.setMaxLifetime(1800000); // 30 minutes
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        
        return dataSource;
    }
}
