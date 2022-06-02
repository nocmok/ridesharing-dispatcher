package com.nocmok.orp.orp_solver;

import com.nocmok.orp.orp_solver.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
		ApplicationConfig.class
})
public class OrpDispatcherApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrpDispatcherApplication.class, args);
	}

}
