package com.nocmok.orp.orp_solver;

import com.nocmok.orp.orp_solver.config.ApplicationConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
		ApplicationConfig.class
})
public class OrpSolverApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrpSolverApplication.class, args);
	}

}
