package com.shinhan.VRRS;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootTest
public class ImageFormatChange {
    @Test
    void contextLoads() throws IOException {
        convertAllImagesInFolder("C:/Users/hyeonji/Documents/카카오톡 받은 파일/products");
    }

    // 특정 폴더에 있는 모든 이미지를 WebP로 변환하는 메서드
    public void convertAllImagesInFolder(String folderPath) throws IOException {
        Path dirPath = Paths.get(folderPath);

        if (!Files.exists(dirPath)) {
            throw new IllegalArgumentException("Folder does not exist: " + folderPath);
        }

        try (Stream<Path> paths = Files.walk(dirPath)) {
            paths.filter(Files::isRegularFile) // 정규 파일만 필터링
                    .filter(path -> isImageFile(path.toString())) // 이미지 파일만 필터링
                    .forEach(this::convertToWebp); // WebP로 변환
        }
    }

    // 이미지 파일 형식 확인 (jpg, png 등)
    private boolean isImageFile(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg") || lowerCaseFileName.endsWith(".png");
    }

    // 이미지 파일을 WebP로 변환하는 메서드
    private void convertToWebp(Path imagePath) {
        try {
            File originalFile = imagePath.toFile();
            String originalFileName = originalFile.getName();  // 원본 파일명
            String baseName = originalFileName.substring(0, originalFileName.lastIndexOf('.'));  // 확장자 제외한 파일명
            String webpFileName = baseName + ".webp";  // 새로운 WebP 파일명

            File webpFile = new File(imagePath.getParent().toString(), webpFileName);  // WebP 파일 저장 경로

            // 이미지 로드 및 WebP로 변환
            ImmutableImage.loader().fromFile(originalFile).output(WebpWriter.DEFAULT, webpFile);

            System.out.println("Converted: " + originalFile.getName() + " to " + webpFile.getName());

            // 변환 후 원본 파일 삭제
            originalFile.delete();
        } catch (IOException e) {
            System.err.println("Error converting image: " + imagePath);
            e.printStackTrace();
        }
    }
}
