package com.bms.bms_app.service;

import com.bms.bms_app.dto.ApiResponse;
import com.bms.bms_app.dto.FileResponse;
import com.bms.bms_app.exception.ResourceNotFoundException;
import com.bms.bms_app.model.FileEntity;
import com.bms.bms_app.repository.FileRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileResponse uploadFile(MultipartFile file, Long relatedId) throws IOException {

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        FileEntity savedFile = FileEntity.builder()
                .fileUrl(filePath.toString())
                .relatedId(relatedId)
                .build();

        fileRepository.save(savedFile);

        return FileResponse.builder()
                .id(savedFile.getId())
                .filePath(savedFile.getFileUrl()).build();
    }


    public FileResponse getFile(Long id) {

        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        FileResponse response = FileResponse.builder()
                .id(file.getId())
                .filePath(file.getFileUrl())
                .build();

        return response;
    }

    public byte[] downloadFile(Long id) throws IOException {

        FileEntity file = fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        Path path = Paths.get(file.getFileUrl());

        byte[] fileData = Files.readAllBytes(path);

        return fileData;
    }

    public void deleteFile(Long fileId) {

        FileEntity file = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        try {
            Path path = Paths.get(file.getFileUrl());
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file from storage");
        }

        fileRepository.delete(file);
    }

}