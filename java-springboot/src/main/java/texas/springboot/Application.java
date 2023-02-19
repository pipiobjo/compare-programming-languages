package texas.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan(basePackages = "com.baeldung.componentscan.springapp.animals")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
