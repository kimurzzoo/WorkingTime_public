package com.workingtime.auth.util.network.email

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class EmailConfig {

    @Value("\${auth.email.username}")
    private var id : String = ""

    @Value("\${auth.email.password}")
    private var password: String = ""

    @Value("\${auth.email.host}")
    private var host: String = ""

    @Value("\${auth.email.port}")
    private var port = 0

    @Bean
    fun javaMailService(): JavaMailSender? {
        val javaMailSender = JavaMailSenderImpl()
        javaMailSender.host = host // smtp 서버 주소
        javaMailSender.username = id // 설정(발신) 메일 아이디
        javaMailSender.password = password // 설정(발신) 메일 패스워드
        javaMailSender.port = port //smtp port
        javaMailSender.javaMailProperties = getMailProperties() // 메일 인증서버 정보 가져온다.
        javaMailSender.defaultEncoding = "UTF-8"
        return javaMailSender
    }

    private fun getMailProperties(): Properties {
        val properties = Properties()
        properties.setProperty("mail.transport.protocol", "smtp") // 프로토콜 설정
        properties.setProperty("mail.smtp.auth", "true") // smtp 인증
        properties.setProperty("mail.smtp.starttls.enable", "true") // smtp starttls 사용
        properties.setProperty("mail.debug", "true") // 디버그 사용
        properties.setProperty("mail.smtp.ssl.trust", host) // ssl 인증 서버 주소
        properties.setProperty("mail.smtp.ssl.enable", "true") // ssl 사용
        return properties
    }
}