package com.shinhan.VRRS.service;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class ImageService {
    @Value("${file.image-dir}")
    private String imageDir;

    @Async
    public void uploadProductImage(MultipartFile file, String imgName) throws IOException {
        // 디렉토리 확인 및 생성
        Path dirPath = Paths.get(imageDir + "/products/");
        if (!Files.exists(dirPath)) Files.createDirectories(dirPath);

        // 임시 파일 생성
        File originalFile = new File(dirPath.toString(), UUID.randomUUID() + ".temp");
        file.transferTo(originalFile);

        // WebP로 변환
        File webpFile = new File(dirPath.toString(), imgName);
        ImmutableImage.loader().fromFile(originalFile).output(WebpWriter.DEFAULT, webpFile);

        if (!originalFile.delete())
            log.error("파일 삭제에 실패했습니다: {}", originalFile.getAbsolutePath());
    }

    public String getImgName() {
        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        return UUID.randomUUID() + "-" + date + ".webp"; // 이미지명
    }

    public Resource getImage(String imgPath) throws MalformedURLException {
        Path file = Paths.get(imageDir).resolve(imgPath);
        return new UrlResource(file.toUri());
    }
}