package com.seoulotakus.takumapbe.global.controller;

import com.seoulotakus.takumapbe.global.service.S3Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    // 파일 업로드
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        return s3Service.uploadFile(file);
    }

    // 파일 삭제
    @DeleteMapping("/delete")
    public String deleteFile(@RequestParam("key") String key) {
        s3Service.deleteFile(key);
        return "삭제 완료: " + key;
    }
}