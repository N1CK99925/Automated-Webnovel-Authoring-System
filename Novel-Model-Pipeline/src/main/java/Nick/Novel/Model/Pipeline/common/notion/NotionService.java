package Nick.Novel.Model.Pipeline.common.notion;

import Nick.Novel.Model.Pipeline.common.config.NotionProperties;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class NotionService {
    private final WebClient webClient;
    private final NotionProperties props;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NotionService(@Qualifier("notionWebClient") WebClient notionWebClient, 
                        NotionProperties props) {
        this.webClient = notionWebClient;
        this.props = props;
    }

    public void saveChapter(String title, String brief, String content) {
        System.out.println("=== NOTION SERVICE DEBUG ===");
        System.out.println("Database ID: " + props.getChapters());
        System.out.println("Title: " + title);
        System.out.println("Brief: " + brief);
        System.out.println("Content length: " + (content != null ? content.length() : 0));
        
        Map<String, Object> properties = Map.of(
            "Name", Map.of("title", List.of(Map.of("text", Map.of("content", title)))),
            "Brief", Map.of("rich_text", List.of(Map.of("text", Map.of("content", brief)))),
            "Status", Map.of("status", Map.of("name", "In progress"))
        );
        
        saveToDatabase(props.getChapters(), properties, content);
    }

    private void saveToDatabase(String databaseId, Map<String, Object> properties, String content) {
        try {
            System.out.println("Creating request body for database: " + databaseId);
            
            Map<String, Object> body = new HashMap<>();
            body.put("parent", Map.of("database_id", databaseId));
            body.put("properties", properties);

            if (content != null) {
                body.put("children", List.of(Map.of(
                    "object", "block",
                    "type", "paragraph",
                    "paragraph", Map.of("rich_text", List.of(Map.of(
                        "type", "text", 
                        "text", Map.of("content", content)
                    )))
                )));
            }

            // Debug: Print the full request body
            try {
                String bodyJson = objectMapper.writeValueAsString(body);
                System.out.println("Request body JSON: " + bodyJson);
            } catch (Exception e) {
                System.err.println("Failed to serialize body for debugging: " + e.getMessage());
            }

            System.out.println("Sending request to Notion API...");
            
            String response = webClient.post()
                    .uri("/pages")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(status -> status.isError(), clientResponse -> {
                        System.err.println("Notion API error status: " + clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("Notion API error body: " + errorBody);
                                    return Mono.error(new RuntimeException("Notion API error: " + errorBody));
                                });
                    })
                    .bodyToMono(String.class)
                    .block();
            
            System.out.println("Notion API success! Response: " + response);
            
        } catch (WebClientResponseException e) {
            System.err.println("HTTP Error: " + e.getStatusCode());
            System.err.println("Response Body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Notion API call failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Test method to verify your configuration
    public void testNotionConnection() {
        try {
            System.out.println("=== TESTING NOTION CONNECTION ===");
            System.out.println("Database ID: " + props.getChapters());
            
            if (props.getChapters() == null || props.getChapters().trim().isEmpty()) {
                System.err.println("ERROR: Chapters database ID is null or empty!");
                return;
            }
            
            String response = webClient.get()
                    .uri("/databases/" + props.getChapters())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
         System.out.println("Connection test successful!");
         System.out.println("Database exists and is accessible");
         System.out.println("Response body: " + response);
            
        } catch (WebClientResponseException e) {
            System.err.println("Connection test failed - HTTP " + e.getStatusCode());
            System.err.println("Error response: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
        }
    }
}