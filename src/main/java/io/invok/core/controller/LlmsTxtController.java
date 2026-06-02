package io.invok.core.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * Controller to serve the llms.txt context file dynamically.
 * In development, it reads directly from the project root directory.
 * In production, it falls back to the packaged classpath resource.
 */
@RestController
public class LlmsTxtController {

    @GetMapping(value = "/llms.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Resource> getLlmsTxt() {
        // Try reading from the project root directory first
        File rootFile = new File("llms.txt");
        if (rootFile.exists() && rootFile.canRead()) {
            return ResponseEntity.ok(new FileSystemResource(rootFile));
        }

        // Fallback to static resource packaged in classpath
        Resource classpathFile = new ClassPathResource("static/browser/llms.txt");
        if (classpathFile.exists() && classpathFile.isReadable()) {
            return ResponseEntity.ok(classpathFile);
        }

        return ResponseEntity.notFound().build();
    }
}
