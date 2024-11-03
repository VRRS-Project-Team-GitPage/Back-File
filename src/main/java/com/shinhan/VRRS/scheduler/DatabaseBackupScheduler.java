package com.shinhan.VRRS.scheduler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class DatabaseBackupScheduler {
    @Value("${file.backup-dir}")
    private String backupDir;

    @Value("${spring.datasource.username}") // 사용자명
    private String dbUser;

    @Value("${spring.datasource.password}") // 비밀번호
    private String dbPassword;

    @Scheduled(fixedRate = 2592000000L) // 30일 간격
    public void backupDatabase() {
        String dbName = "infodb";

        // 현재 날짜를 기반으로 파일 이름 생성
        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        String backupFileName = "/backup_" + date + ".sql";

        try {
            // 디렉토리 확인 및 생성
            Path dirPath = Paths.get(backupDir);
            if (!Files.exists(dirPath)) Files.createDirectories(dirPath);

            // mysqldump 명령어 실행
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "mysqldump",
                    "-u", dbUser,
                    "--password=" + dbPassword,
                    dbName,
                    "--result-file=" + backupDir + backupFileName
            );

            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0)
                log.info("Database backup completed successfully.");
            else
                log.error("Backup failed with exit code: {}", exitCode);
        } catch (IOException e) {
            log.error("IOException occurred during backup: {}", e.getMessage(), e);
        } catch (InterruptedException e) {
            log.error("InterruptedException occurred during backup: {}", e.getMessage(), e);
        }
    }
}