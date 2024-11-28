package ua.edu.internship.interview.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Value("${project.version}")
    private String version;
    @Value("${domain.back}")
    private String devServer;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Interview")
                        .version(version)
                        .description("Interview service API."))
                .servers(List.of(new Server().url(devServer)));
    }
}
