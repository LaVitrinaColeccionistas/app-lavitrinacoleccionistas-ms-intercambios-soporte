package py.com.lavitrinacoleccionistas.intercambiossoporte.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI intercambioSoporteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("La Vitrina Coleccionistas - Intercambios y Soporte")
                        .description(
                                "API REST del microservicio encargado de la gestión " +
                                        "de intercambios entre usuarios y tickets de soporte."
                        )
                        .version("1.0.0"));
    }
}