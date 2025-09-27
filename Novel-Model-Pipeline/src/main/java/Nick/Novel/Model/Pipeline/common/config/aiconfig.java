package Nick.Novel.Model.Pipeline.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class aiconfig {
    @Bean(name = "aiwebclient")
    public WebClient aiWebClient(WebClient.Builder builder){
        return builder
        .baseUrl("http://localhost:8000") // FastAPI service
            .defaultHeader("Content-Type", "application/json")
            .build();
    }
    }

