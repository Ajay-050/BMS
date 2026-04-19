package com.bms.bms_app.service;

import com.bms.bms_app.model.File;
import com.bms.bms_app.repository.FileRepository;
import jakarta.persistence.*;
import lombok.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.springframework.web.bind.annotation.*;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public File uploadFile(MultipartFile file, Long relatedId) throws IOException {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        File savedFile = File.builder()
                .fileUrl(filePath.toString())
                .relatedId(relatedId)
                .build();

        return fileRepository.save(savedFile);
    }
}