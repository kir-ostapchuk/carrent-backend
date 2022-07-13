package com.ostapchuk.email.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Properties;

import static com.ostapchuk.email.service.util.Constant.MAIL_SMTP_AUTH;
import static com.ostapchuk.email.service.util.Constant.MAIL_SMTP_HOST;
import static com.ostapchuk.email.service.util.Constant.MAIL_SMTP_PORT;
import static com.ostapchuk.email.service.util.Constant.MAIL_SMTP_SSL_ENABLE;
import static com.ostapchuk.email.service.util.Constant.PORT_MAIL_RU;
import static com.ostapchuk.email.service.util.Constant.SMTP_MAIL_RU;
import static java.lang.Boolean.TRUE;

@Configuration
public class MailRuSmtpConfig {

    @Bean
    @Primary
    public Properties mailRuSmtp() {
        final Properties properties = System.getProperties();
        properties.put(MAIL_SMTP_HOST, SMTP_MAIL_RU);
        properties.put(MAIL_SMTP_PORT, PORT_MAIL_RU);
        properties.put(MAIL_SMTP_SSL_ENABLE, TRUE);
        properties.put(MAIL_SMTP_AUTH, TRUE);
        return properties;
    }
}
