package com.shop.Main.payload.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    SpringTemplateEngine springTemplateEngine;

    public void sendEmail(MailRequest request) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );
            Context context = new Context();
            context.setVariables(request.getModel());
            String html = springTemplateEngine.process("EmailForgotPasswordPage", context);

            helper.setTo(request.getTo());
            helper.setText(html, true);
            helper.setSubject(request.getSubject());
            helper.setFrom(request.getFrom());

            javaMailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
