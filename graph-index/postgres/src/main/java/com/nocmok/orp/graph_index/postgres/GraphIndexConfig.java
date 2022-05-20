package com.nocmok.orp.graph_index.postgres;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan("com.nocmok.orp.graph_index.postgres")
@PropertySources({
        @PropertySource("classpath:graph_index999.properties")
})
public class GraphIndexConfig {
}
