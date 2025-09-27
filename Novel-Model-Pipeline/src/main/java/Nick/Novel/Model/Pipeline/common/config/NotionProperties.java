package Nick.Novel.Model.Pipeline.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@ConfigurationProperties(prefix = "notion.databases")
public class NotionProperties {
    private String chapters;
    private String characters;
    private String lore;


}
