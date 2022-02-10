package com.nocmok.orp.orp_solver.config;

import com.nocmok.orp.state_keeper.pg.StateKeeperPostgres;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class VehicleStateServiceConfig {

    @Value("${pg.db.orp.username}")
    private String orpDbUserName;
    @Value("${pg.db.orp.password}")
    private String orpDbPassword;
    @Value("${pg.db.orp.url}")
    private String orpDbUrl;

    @Autowired
    private JacksonConfig jacksonConfig;

    @Bean
    public DataSource vehicleStateDatasource() {
        var config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setUsername(orpDbUserName);
        config.setPassword(orpDbPassword);
        config.setJdbcUrl(orpDbUrl);
        return new HikariDataSource(config);
    }

    @Bean
    public StateKeeperPostgres vehicleVehicleStateService() {
        return new StateKeeperPostgres(vehicleStateDatasource(), jacksonConfig.objectMapper());
    }
}
