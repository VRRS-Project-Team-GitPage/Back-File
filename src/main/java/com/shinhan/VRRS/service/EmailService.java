package com.shinhan.VRRS.service;

import com.shinhan.VRRS.dto.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public String sendMail(EmailMessage emailMassage, String type) throws MessagingException {
        String code = createCode(); // 인증번호 및 임시 비밀번호 생성

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

        String fromName = "채식어디";
        String fromEmail = username + "@gmail.com";

        mimeMessageHelper.setFrom(fromName + " <" + fromEmail + ">");
        mimeMessageHelper.setTo(emailMassage.getTo()); // 메일 수신자
        mimeMessageHelper.setSubject(emailMassage.getSubject()); // 메일 제목
        mimeMessageHelper.setText(setContext(code, type), true); // 메일 본문 내용, HTML 여부
        javaMailSender.send(mimeMessage);
        return code;
    }

    public String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);
            switch (index) {
                case 0:
                    key.append((char) ((int) random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int) random.nextInt(26) + 65));
                    break;
                default:
                    key.append(random.nextInt(9));
            }
        }
        return key.toString();
    }

    public String setContext(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(type, context);
    }
}