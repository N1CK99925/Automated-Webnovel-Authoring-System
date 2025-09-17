package Nick.Novel.Model.Pipeline.generator;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/generator")
@RequiredArgsConstructor
public class GenerateChapterController {
    private final GenerateChapterService generateChapterService;

    @PostMapping("/chapter")
    public ResponseEntity<?> GenerateChapter(@RequestParam String brief) {
        String draft = generateChapterService.generateChapter(brief);
        
        return ResponseEntity.ok(draft);
    }
    
}
