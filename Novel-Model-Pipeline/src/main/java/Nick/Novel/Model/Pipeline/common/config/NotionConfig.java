package Nick.Novel.Model.Pipeline.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NotionConfig {

    @Value("${notion.api.token}")
    private String notionToken;

    @Bean(name = "notionWebClient")
    public WebClient notionWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://api.notion.com/v1")
                .defaultHeader("Authorization", "Bearer " + notionToken)
                .defaultHeader("Notion-Version", "2022-06-28") // Use stable version
                .defaultHeader("Content-Type", "application/json")
                .codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(2 * 1024 * 1024)) // 2MB buffer for large content
                .build();
    }
}