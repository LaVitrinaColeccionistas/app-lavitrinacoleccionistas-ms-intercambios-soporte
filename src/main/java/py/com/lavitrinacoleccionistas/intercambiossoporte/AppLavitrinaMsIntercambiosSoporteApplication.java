package py.com.lavitrinacoleccionistas.intercambiossoporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "py.com.lavitrinacoleccionistas.entity")
public class AppLavitrinaMsIntercambiosSoporteApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppLavitrinaMsIntercambiosSoporteApplication.class, args);
	}

}
