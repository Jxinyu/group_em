package com.gp.service.impl;

import com.gp.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author ljb
 * @create 2023/9/21
 */
@Service
public class EmailServiceImpl implements EmailService {
    @Resource
    private JavaMailSender emailSender;

    private String getMailFrom() {
        return System.getenv().getOrDefault("MAIL_USERNAME", "");
    }

    /**
     * 发送简单邮件
     *
     * @param to      发给谁
     * @param subject 主题
     * @param text    内容
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        message.setFrom(getMailFrom());

        emailSender.send(message);
    }

    /**
     * 发送带附件的邮件
     * @param to      发给谁
     * @param subject 主题
     * @param content 内容
     */
    public void sendAnnexEmail(String to, String subject, String content) {
        // 解决本地DNS未配置 ip->域名场景下，邮件发送太慢的问题
        System.getProperties().setProperty("mail.mime.address.usecanonicalhostname", "false");
        //创建一个MINE消息
        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //发送人
            helper.setFrom(getMailFrom());
            //接收人
            helper.setTo(to);
            //邮件主题
            helper.setSubject(subject);
            //邮件内容   true 表示带有附件或html
            helper.setText(content, true);

            // String fileName = file.getName();
            // //添加附件
            //helper.addAttachment(fileName, file);

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步发送邮件
     *
     * @param to  邮箱
     * @param content 信息
     * @param file    附件路劲
     * @param subject 主题
     */
    @Async
    public void groupEmail(String to, String subject,  String content, String file){
        // 解决本地DNS未配置 ip->域名场景下，邮件发送太慢的问题
        System.getProperties().setProperty("mail.mime.address.usecanonicalhostname", "false");
        //创建一个MINE消息
        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            //发送人
            helper.setFrom(getMailFrom());
            //接收人
            helper.setTo(to);
            //邮件主题
            helper.setSubject(subject);
            //邮件内容   true 表示带有附件或html
            helper.setText(content, true);
            //添加附件
            if (file != null){
                helper.addAttachment(subject, new File(file));
            }
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
