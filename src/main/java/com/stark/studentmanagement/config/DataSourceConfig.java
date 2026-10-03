package com.stark.studentmanagement.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private static final String DEFAULT_NEON_URL = "jdbc:postgresql://ep-sparkling-bird-axklmy5h-pooler.c-4.us-east-2.aws.neon.tech/neondb?sslmode=require";
    private static final String DEFAULT_NEON_USER = "neondb_owner";
    private static final String DEFAULT_NEON_PASS = "npg_HCOPJnw1sKx2";

    @Autowired
    private Environment environment;

    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        
        String url = environment.getProperty("SPRING_DATASOURCE_URL");
        if (url == null || url.trim().isEmpty()) {
            url = environment.getProperty("spring.datasource.url");
        }
        
        String user = environment.getProperty("SPRING_DATASOURCE_USERNAME");
        if (user == null || user.trim().isEmpty()) {
            user = environment.getProperty("spring.datasource.username");
        }
        
        String pass = environment.getProperty("SPRING_DATASOURCE_PASSWORD");
        if (pass == null || pass.trim().isEmpty()) {
            pass = environment.getProperty("spring.datasource.password");
        }

        // If URL is missing, empty, or contains dummy placeholder text, fallback to live Neon PostgreSQL
        if (url == null || url.trim().isEmpty() || url.contains("<your-mysql-host>") || url.contains("<") || url.contains(">")) {
            properties.setUrl(DEFAULT_NEON_URL);
            properties.setUsername(DEFAULT_NEON_USER);
            properties.setPassword(DEFAULT_NEON_PASS);
            properties.setDriverClassName("org.postgresql.Driver");
        } else {
            properties.setUrl(url);
            properties.setUsername(user != null && !user.trim().isEmpty() ? user : DEFAULT_NEON_USER);
            properties.setPassword(pass != null && !pass.trim().isEmpty() ? pass : DEFAULT_NEON_PASS);
            
            if (url.startsWith("jdbc:postgresql")) {
                properties.setDriverClassName("org.postgresql.Driver");
            } else if (url.startsWith("jdbc:mysql")) {
                properties.setDriverClassName("com.mysql.cj.jdbc.Driver");
            } else if (url.startsWith("jdbc:h2")) {
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
