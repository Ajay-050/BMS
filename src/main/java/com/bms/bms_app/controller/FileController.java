package com.bms.bms_app.controller;

import com.bms.bms_app.dto.ApiResponse;
import com.bms.bms_app.dto.FileResponse;
import com.bms.bms_app.model.FileEntity;
import com.bms.bms_app.repository.FileRepository;
import com.bms.bms_app.service.FileService;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private FileRepository fileRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("relatedId") Long relatedId) {

        try {
            FileResponse uploaded = fileService.uploadFile(file, relatedId);
            ApiResponse<FileResponse> response = new ApiResponse<>(true,"File Uploaded Successfully",uploaded);
            return ResponseEntity.status(CREATED).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFile(@PathVariable Long id) {

        FileResponse response = fileService.getFile(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "File fetched", response)
        );
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id) throws IOException {

        byte[] FileData = fileService.downloadFile(id);

        return ResponseEntity.ok()
                .header("Content-Disposition")
                .body(FileData);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<?> removeFile(@PathVariable Long id) {

        fileService.deleteFile(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "File deleted successfully", null)
        );
    }

}