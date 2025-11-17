package biblioteca.onliine.biblioteca.interfaces.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins(
                            "http://localhost:8080",
                            "http://localhost:5173",
                            "http://localhost:5175",
                            "https://vacciniaceous-bryn-unperfumed.ngrok-free.dev/"
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .exposedHeaders("X-Can-Download") // ESSA LINHA É OBRIGATÓRIA
                    .allowCredentials(true);
        }


}
