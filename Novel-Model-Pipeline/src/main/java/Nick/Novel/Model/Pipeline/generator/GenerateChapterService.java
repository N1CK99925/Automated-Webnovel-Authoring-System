package Nick.Novel.Model.Pipeline.generator;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GenerateChapterService {
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8000").build();

    public String generateChapter(String brief) {
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

    // Helper function to enforce word limit
    private String enforceWordLimit(String text, int wordLimit) {
        String[] words = text.split("\\s+");
        if (words.length <= wordLimit) return text;
        return String.join(" ", java.util.Arrays.copyOfRange(words, 0, wordLimit));
    }
}
