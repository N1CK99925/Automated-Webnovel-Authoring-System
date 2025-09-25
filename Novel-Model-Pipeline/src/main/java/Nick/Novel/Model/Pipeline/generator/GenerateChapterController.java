package Nick.Novel.Model.Pipeline.generator;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Nick.Novel.Model.Pipeline.common.notion.NotionService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/generator")
@RequiredArgsConstructor
public class GenerateChapterController {
    private final GenerateChapterService generateChapterService;
    private final NotionService notionService;

    @PostMapping("/chapter")
    public ResponseEntity<?> GenerateChapter(@RequestParam String brief) {
        String draft = generateChapterService.generateChapter(brief);
        
        notionService.saveChapter(
            "Chapter Draft", //TODO: Make this dynamic bleh
            brief, 
            draft);
            
        return ResponseEntity.ok(draft);
    }
    
}
