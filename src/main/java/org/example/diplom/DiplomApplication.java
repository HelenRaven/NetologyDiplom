package org.example.diplom;

import jakarta.annotation.Resource;
import org.example.diplom.service.FilesStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiplomApplication implements CommandLineRunner {
    @Resource
    FilesStorageService storageService;

    public static void main(String[] args) {
        SpringApplication.run(DiplomApplication.class, args);
    }

    @Override
    public void run(String... arg) throws Exception {
        storageService.init();
    }
}
