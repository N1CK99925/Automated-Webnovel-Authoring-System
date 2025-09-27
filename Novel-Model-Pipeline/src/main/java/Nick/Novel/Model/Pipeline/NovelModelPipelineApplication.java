package Nick.Novel.Model.Pipeline;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import Nick.Novel.Model.Pipeline.common.config.NotionProperties;


@EnableConfigurationProperties(NotionProperties.class)
@SpringBootApplication
public class NovelModelPipelineApplication {

	public static void main(String[] args) {
		SpringApplication.run(NovelModelPipelineApplication.class, args);
	}
	@Bean
    CommandLineRunner testProps(NotionProperties props) {
        return _ -> {
            System.out.println("✅ Chapters DB = " + props.getChapters());
            System.out.println("✅ Characters DB = " + props.getCharacters());
            System.out.println("✅ Realms DB = " + props.getLore());
        
            
        };
	
}

}
