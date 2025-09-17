package Nick.Novel.Model.Pipeline.generator;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

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
                .bodyToMono(String.class)
                .block();
    }
}
