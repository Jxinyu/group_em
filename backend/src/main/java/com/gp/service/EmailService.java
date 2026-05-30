package com.gp.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    /**
     * 发送简单邮件
     *
     * @param to      发给谁
     * @param subject 主题
     * @param text    内容
     */
    public void sendSimpleEmail(String to, String subject, String text);

    /**
     * 发送带附件的邮件
     * @param to      发给谁
     * @param subject 主题
     * @param content 内容
     */
    public void sendAnnexEmail(String to, String subject, String content);
    /**
     * 异步发送邮件
     *
     * @param to  邮箱
     * @param content 信息
     * @param file    附件路劲
     * @param subject 主题
     */
    @Async
    public void groupEmail(String to, String subject,  String content, String file);


}