package controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConveyorApplication {
    @Operation(
            summary = "Запуск преложения",
            description = "Позволяет запустить преложение")
    public static void main(String[] args) {
        SpringApplication.run(ConveyorApplication.class, args);
    }
}
