package vn.feylix;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import vn.feylix.config.StorageProperties;
import vn.feylix.services.IStorageService;


@SpringBootApplication(scanBasePackages = { "vn.feylix", "org.springdoc" })
@EnableConfigurationProperties(StorageProperties.class)
public class Baitap8Quest1Application {

	public static void main(String[] args) {
		SpringApplication.run(Baitap8Quest1Application.class, args);
	}

	@Bean
	CommandLineRunner init(IStorageService storageService) {
		return (args -> {
			storageService.init();
		});
	}

}
