package com.shinhan.VRRS.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProductImageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String loadProductImage(MultipartFile image) throws IOException {
        String imgName = UUID.randomUUID() + "-" + image.getOriginalFilename(); // 이미지명
        String dbImgPath = "/products/" + imgName; // DB에 저장할 이미지 경로

        Path dirPath = Paths.get(uploadDir + "/products/"); // 디렉토리 경로
        if (!Files.exists(dirPath)) Files.createDirectories(dirPath); // 디렉토리 생성

        Path imgPath = dirPath.resolve(imgName); // 이미지 경로
        Files.write(imgPath, image.getBytes()); // 이미지 저장
        return dbImgPath;
    }

    public Resource getProductImage(@PathVariable String imgPath) throws MalformedURLException {
        Path file = Paths.get(uploadDir).resolve(imgPath);
        return new UrlResource(file.toUri());
    }
}