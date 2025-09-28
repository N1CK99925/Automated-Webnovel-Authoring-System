package Nick.Novel.Model.Pipeline.common.notion;

import Nick.Novel.Model.Pipeline.common.config.NotionProperties;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


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

   public Mono<String> saveChapterReactive(String title, String brief, String content) {
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

    return saveToDatabaseReactive(props.getChapters(), properties, content);
}

private Mono<String> saveToDatabaseReactive(String databaseId, Map<String, Object> properties, String content) {
    Map<String, Object> body = new HashMap<>();
    body.put("parent", Map.of("database_id", databaseId));
    body.put("properties", properties);

    if (content != null) {
         List<Map<String, Object>> children = splitContentIntoBlocks(content);
        body.put("children", children);
    }
    

    // Debug: Print the full request body
    try {
        String bodyJson = objectMapper.writeValueAsString(body);
        System.out.println("Request body JSON: " + bodyJson);
    } catch (Exception e) {
        System.err.println("Failed to serialize body for debugging: " + e.getMessage());
    }

    return webClient.post()
        .uri("/pages")
        .bodyValue(body)
        .retrieve()
        .onStatus(status -> status.isError(), response ->
            response.bodyToMono(String.class).flatMap(errorBody -> {
                System.err.println("Notion API error body: " + errorBody);
                return Mono.error(new RuntimeException(errorBody));
            })
        )
        .bodyToMono(String.class)
        .doOnNext(res -> System.out.println("Notion API success: " + res));

}
// split it into multiple chunks if its too long for notion to fit it in one cell, 2000 hota hai eak ka
private List<Map<String, Object>> splitContentIntoBlocks(String content) {
    List<Map<String, Object>> blocks = new ArrayList<>();
    int maxLength = 2000;
    
    for (int i = 0; i < content.length(); i += maxLength) {
        int end = Math.min(content.length(), i + maxLength);
        String chunk = content.substring(i, end);
        
        Map<String, Object> block = Map.of(
            "object", "block",
            "type", "paragraph",
            "paragraph", Map.of("rich_text", List.of(Map.of(
                "type", "text",
                "text", Map.of("content", chunk)
            )))
        );
        
        blocks.add(block);
    }
    
    System.out.println("Split content into " + blocks.size() + " blocks");
    return blocks;
}
}