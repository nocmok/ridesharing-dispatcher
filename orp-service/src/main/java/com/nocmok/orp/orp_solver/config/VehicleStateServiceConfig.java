package com.nocmok.orp.orp_solver.config;

import com.nocmok.orp.core_api.Vehicle;
import com.nocmok.orp.core_api.VehicleStateService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
    public VehicleStateService<com.nocmok.orp.vss.pg.Vehicle> vehicleVehicleStateService() {
        return new com.nocmok.orp.vss.pg.VehicleStateService(vehicleStateDatasource());
    }
}
