package com.ekino.team2.punctuality.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class MyMailSender {

    private static Logger logger = LoggerFactory.getLogger(MyMailSender.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${mail.sendto}")
    private String sendto;

    @Value("${mail.sendcc1}")
    private String sendcc1;
    @Value("${mail.sendcc2}")
    private String sendcc2;
    @Value("${mail.sendcc3}")
    private String sendcc3;
    @Value("${mail.sendcc4}")
    private String sendcc4;
    @Value("${mail.sendcc5}")
    private String sendcc5;
    @Value("${mail.sendcc6}")
    private String sendcc6;


    public void sendEmailWithAttachment(String fileName) {
        try {

            MimeMessage msg = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(sendto);
            helper.addCc(sendcc1);
            helper.addCc(sendcc2);
            helper.addCc(sendcc3);
            helper.addCc(sendcc4);
            helper.addCc(sendcc5);
            helper.addCc(sendcc6);

            helper.setSubject("Daily fingerprint :" + fileName);
            helper.setText("Data finger day : " + fileName, false);

            FileSystemResource file = new FileSystemResource(new File("excelMailDaily/" + fileName + ".xlsx"));

            if (file.exists()) {
                helper.addAttachment(fileName + ".xlsx", file);
                javaMailSender.send(msg);
            }
            logger.info("Send email with attachment success {} :", fileName);

        } catch (Exception e) {
            logger.error("Send email with attachment err :", e);
        }

    }
}
