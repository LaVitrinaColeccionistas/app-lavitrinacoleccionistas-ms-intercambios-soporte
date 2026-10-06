package py.com.lavitrinacoleccionistas.intercambiossoporte;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "py.com.lavitrinacoleccionistas.entity")
@OpenAPIDefinition(servers = @Server(url = "/api"))
public class AppLavitrinaMsIntercambiosSoporteApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppLavitrinaMsIntercambiosSoporteApplication.class, args);
	}

}
