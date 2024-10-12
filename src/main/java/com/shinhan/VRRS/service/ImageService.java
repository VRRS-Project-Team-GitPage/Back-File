package com.shinhan.VRRS.service;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String uploadProductImage(MultipartFile image) throws IOException {
        String imgName = UUID.randomUUID() + ".webp"; // 이미지명
        String dbImgPath = "/products/" + imgName; // DB에 저장할 이미지 경로

        // 디렉토리 확인 및 생성
        Path dirPath = Paths.get(uploadDir + "/products/");
        if (!Files.exists(dirPath)) Files.createDirectories(dirPath);

        // 임시 파일 생성
        File originalFile = new File(dirPath.toString(), UUID.randomUUID() + ".temp");
        image.transferTo(originalFile);

        // WebP로 변환
        File webpFile = new File(dirPath.toString(), imgName);
        ImmutableImage.loader().fromFile(originalFile).output(WebpWriter.DEFAULT, webpFile);

        originalFile.delete(); // 임시 파일 삭제

        return dbImgPath;
    }

    public Resource getImage(String imgPath) throws MalformedURLException {
        Path file = Paths.get(uploadDir).resolve(imgPath);
        return new UrlResource(file.toUri());
    }
}