package com.shinhan.VRRS.service.productService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProductImageService {
    private final static String uploadDir = "src/main/resources/static/uploads/products/";

    public String saveProductImage(MultipartFile image) throws IOException {
        String imageName = UUID.randomUUID() + "-" + image.getOriginalFilename(); // 이미지명
        String imagePath = uploadDir + imageName; // 이미지 저장 경로
        String dbImagePath = "/uploads/products/" + imageName; // DB에 저장할 이미지 경로

        Path path = Paths.get(imagePath); // Path 객체 생성
        Files.write(path, image.getBytes()); // 디렉토리에 파일 저장
        return dbImagePath;
    }
}