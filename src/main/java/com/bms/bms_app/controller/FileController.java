package com.bms.bms_app.controller;

import com.bms.bms_app.model.File;
import com.bms.bms_app.service.FileService;
import jakarta.persistence.*;
import lombok.*;

import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;



@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("relatedId") Long relatedId) {

        try {
            File uploaded = fileService.uploadFile(file, relatedId);
            return ResponseEntity.ok(uploaded);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed");
        }
    }
}