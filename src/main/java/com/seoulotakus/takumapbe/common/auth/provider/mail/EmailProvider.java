package com.seoulotakus.takumapbe.common.auth.provider.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    private final String SUBJECT = "[TAKUMAP 서비스] 인증 메일입니다.";

    // 메일 전송 메소드
    public boolean sendCertificationMail(String email, String certificationNumber){

        try{

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getCertificationMessage(certificationNumber);

            messageHelper.setTo(email);  // 메일 받을 주소 설정
            messageHelper.setSubject(SUBJECT);  // 이메일 제목 설정
            messageHelper.setText(htmlContent, true);  // 이메일 내용 설정

            // 메일 전송
            javaMailSender.send(message);

        } catch(Exception exception){
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    // 전송될 이메일 내용 작성하는 메소드
    private String getCertificationMessage(String certificationNumber){

        String certificationMessage = "";

        certificationMessage += "<h1 style='text-align: center;'>[TAKUMAP 서비스] 인증 메일</h1>";
        certificationMessage += "<h3 style='text-align: center;'>인증 코드 : <strong style='font-size: 32px;, letter-spacing: 8px;'>" + certificationNumber + "</strong></h3>";

        return certificationMessage;
    }
}
