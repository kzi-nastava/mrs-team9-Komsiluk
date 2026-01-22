package rs.ac.uns.ftn.iss.Komsiluk;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import rs.ac.uns.ftn.iss.Komsiluk.security.jwt.JwtProperties;

@EnableConfigurationProperties(JwtProperties.class)
@EnableScheduling
@SpringBootApplication
public class KomsilukTaxiProjectApplication {
	
	@Bean
	public ModelMapper getModelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(KomsilukTaxiProjectApplication.class, args);
	}

}
