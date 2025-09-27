package Nick.Novel.Model.Pipeline.generator;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import Nick.Novel.Model.Pipeline.common.notion.LoreResponse;
import reactor.core.publisher.Mono;

@Service

public class GenerateChapterService {
    @Qualifier("aiwebclient")
    private final WebClient webClient;

    // Make word limit configurable via application.properties
    @Value("${novel.chapter.word-limit:2000}")
    private int wordLimit;

    // constructor to define webclietn cause lombok is doing empty constructor so it uses notion bean instead
    public GenerateChapterService(@Qualifier("aiwebclient") WebClient webClient){
        this.webClient = webClient;
    }
    /**
     * Generates a chapter from a brief.
     * Returns a Mono<String> for async handling.
     */
    public Mono<String> generateChapter(String brief) {
        return webClient.post()
                .uri("/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("brief", brief))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                .map(response -> {
                    String chapter = response.getOrDefault("text", "");
                    return enforceWordLimit(chapter, wordLimit);
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    // Log the error and return empty chapter
                    System.err.println("Error calling AI service: " + ex.getMessage());
                    return Mono.just("");
                });
    }
    
    public Mono<LoreResponse> extractLore(String story){
        return webClient.post()
        .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of("brief", story)) 
            .retrieve()
            .bodyToMono(LoreResponse.class)
            .onErrorResume(WebClientResponseException.class, ex -> {
                System.err.println("Error calling Lore extractor: " + ex.getMessage());
                return Mono.empty();
            });
    }

    // Helper function to enforce word limit
    private String enforceWordLimit(String text, int wordLimit) {
        String[] words = text.split("\\s+");
        if (words.length <= wordLimit) return text;
        return String.join(" ", java.util.Arrays.copyOfRange(words, 0, wordLimit));
    }
}
