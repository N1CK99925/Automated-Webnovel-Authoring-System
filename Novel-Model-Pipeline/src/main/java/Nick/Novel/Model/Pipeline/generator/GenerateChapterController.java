package Nick.Novel.Model.Pipeline.generator;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Nick.Novel.Model.Pipeline.common.notion.NotionService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/generator")
@RequiredArgsConstructor
public class GenerateChapterController {

    private final GenerateChapterService generateChapterService;
    private final NotionService notionService;

    @PostMapping("/chapter")
    public Mono<ResponseEntity<String>> generateChapter(@RequestParam String brief) {
        return generateChapterService.generateChapter(brief)
                .flatMap(draft -> {
                    // Save to Notion (side effect)
                    return notionService.saveChapterReactive(
                            "Chapter Draft", // TODO: Make dynamic
                            brief,
                            draft
                    ).then(Mono.just(ResponseEntity.ok(draft)));
                });
    }
}
