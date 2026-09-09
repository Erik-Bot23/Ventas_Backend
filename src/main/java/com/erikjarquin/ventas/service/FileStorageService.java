package com.erikjarquin.ventas.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadDir; //

    public FileStorageService(@Value("${app.upload-dir:./uploads}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize(); //
        try {
            Files.createDirectories(this.uploadDir); //
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads: " + this.uploadDir, e);
        }
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        //
        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = "";
        int dot = original.lastIndexOf('.'); //

        if (dot >= 0) {
            extension = original.substring(dot).toLowerCase();
        }
        
        String storedName = UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = this.uploadDir.resolve(storedName).normalize();
        
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar el archivo: " + storedName, e);
        }
        return storedName;
    }

    public void delete(String storedName) {
        if (storedName == null || storedName.isEmpty()) {
            return;
        }
        try {
            Files.deleteIfExists(this.uploadDir.resolve(storedName).normalize());
        } catch (IOException e) {
            throw new RuntimeException("No se pudo eliminar el archivo: " + storedName, e);
        }
    }
}