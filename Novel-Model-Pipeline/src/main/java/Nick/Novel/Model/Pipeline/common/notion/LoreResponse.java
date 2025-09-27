package Nick.Novel.Model.Pipeline.common.notion;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoreResponse {
    private String text;
    private Metadata metadata;

    // getters/setters
    @Getter
    @Setter
    public static class Metadata {
        private List<CharacterInfo> characters;
        private List<LoreInfo> lore;
        // getters/setters
    }
    @Getter
    @Setter
    public static class CharacterInfo {
      private String name;
      private String clan;
      private String role;
      private String status;
      private String lastAppearance;
      private String details;
    }
    @Getter
    @Setter
    public static class LoreInfo {
        private String name;
        private String type;
        private String details;
        // getters/setters
    }
}
