package com.backlogforge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@SpringBootApplication
public class BacklogForgeApplication {

    public static void main(String[] args) {
        loadDotEnvIfPresent();
        SpringApplication.run(BacklogForgeApplication.class, args);
    }

    private static void loadDotEnvIfPresent() {
        List<File> possibleEnvFiles = List.of(
                new File(".env"),
                new File("../.env"),
                new File("../../.env")
        );

        for (File envFile : possibleEnvFiles) {
            if (envFile.exists() && envFile.isFile()) {
                try {
                    List<String> lines = Files.readAllLines(envFile.toPath());
                    for (String line : lines) {
                        String trimmed = line.trim();
                        if (trimmed.isEmpty() || trimmed.startsWith("#") || !trimmed.contains("=")) {
                            continue;
                        }
                        int eqIdx = trimmed.indexOf('=');
                        String key = trimmed.substring(0, eqIdx).trim();
                        String value = trimmed.substring(eqIdx + 1).trim();

                        if (!key.isEmpty() && !value.isEmpty() && System.getenv(key) == null && System.getProperty(key) == null) {
                            System.setProperty(key, value);
                        }
                    }
                } catch (Exception ignored) {
                }
                break;
            }
        }
    }
}
