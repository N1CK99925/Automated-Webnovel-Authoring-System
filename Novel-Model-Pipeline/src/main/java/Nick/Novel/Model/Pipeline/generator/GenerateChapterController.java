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
        .flatMap(draft -> 
            notionService.saveChapterReactive("Chapter Draft", brief, draft)
                .then(generateChapterService.extractLore(draft)) 
                .flatMap(loreResponse -> {
                    Mono<Void> charactersMono = Mono.when(
                        loreResponse.getMetadata().getCharacters().stream()
                            .map(c -> notionService.saveCharacter(
                                    c.getName(),
                                    c.getClan(),
                                    c.getRole(),
                                    c.getStatus(),
                                    c.getLastAppearance(),
                                    c.getDetails()
                            ))
                            .toList()
                    );

                    // 4️⃣ Save all lore
                    Mono<Void> loreMono = Mono.when(
                        loreResponse.getMetadata().getLore().stream()
                            .map(l -> notionService.saveLore(
                                    l.getName(),
                                    l.getType(),
                                    l.getDetails()
                            ))
                            .toList()
                    );

                    // 5️⃣ Wait for all saves to complete
                    return Mono.when(charactersMono, loreMono)
                               .then(Mono.just(ResponseEntity.ok(draft)));
                })
        );
}

}
