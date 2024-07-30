package com.ynshb.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String to, String username, EmailTemplate emailTemplate, String confirmationUrl,
                          String activateCode, String subject) throws MessagingException {
        String templateName = (Objects.isNull(emailTemplate)) ? "confirm-email" : emailTemplate.getName();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
        Map<String, Object> properties = Map.of(
                "username", username,
                "confirmationUrl", confirmationUrl,
                "activationCode", activateCode
        );
        Context context = new Context();
        context.setVariables(properties);

        mimeHelper.setFrom("contact@ynshb.com");
        mimeHelper.setTo(to);
        mimeHelper.setSubject(subject);

        String template = templateEngine.process(templateName, context);
        mimeHelper.setText(template, true);
        mailSender.send(mimeMessage);
    }
}
