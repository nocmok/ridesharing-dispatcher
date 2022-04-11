package com.nocmok.orp.telemetry.config.postgres;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

@Configuration
public class PostgresConfig {

    @Value("${pg.db.orp.username}")
    private String orpDbUserName;
    @Value("${pg.db.orp.password}")
    private String orpDbPassword;
    @Value("${pg.db.orp.url}")
    private String orpDbUrl;

    @Bean
    public DataSource datasource() {
        var config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setUsername(orpDbUserName);
        config.setPassword(orpDbPassword);
        config.setJdbcUrl(orpDbUrl);
        return new HikariDataSource(config);
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        var jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(datasource());
        jdbcTemplate.setQueryTimeout(10_000);
        jdbcTemplate.setFetchSize(1000);
        return jdbcTemplate;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        var jdbcTemplate = new NamedParameterJdbcTemplate(datasource());
        return jdbcTemplate;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        var transactionManager = new DataSourceTransactionManager(datasource());
        return transactionManager;
    }

    @Bean
    public TransactionTemplate transactionTemplate() {
        var transactionTemplate = new TransactionTemplate(platformTransactionManager());
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionTemplate.setTimeout(-1);
        return transactionTemplate;
    }
}
