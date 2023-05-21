package com.workingtime.auth.util.network.email

import com.workingtime.auth.util.network.dto.ResponseDTO
import com.workingtime.auth.util.redis.RedisUtil
import jakarta.mail.MessagingException
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.io.UnsupportedEncodingException
import java.util.*


data class MessageContainer(
    val ePw : String,
    val message : MimeMessage
)

@Service
class EmailService(private val javaMailSender: JavaMailSender,
                    private val redisUtil: RedisUtil) {

    @Value("\${auth.email.username}")
    private val id: String = ""

    @Throws(MessagingException::class, UnsupportedEncodingException::class)
    fun createMessage(to: String): MessageContainer {
        val ePw : String = createKey()
        val message = javaMailSender.createMimeMessage()
        message.addRecipients(MimeMessage.RecipientType.TO, to) // to 보내는 대상
        message.subject = "Working Time 회원가입 인증 코드: " //메일 제목

        // 메일 내용 메일의 subtype을 html로 지정하여 html문법 사용 가능
        var msg: String? = ""
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>"
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 확인 코드를 회원가입 화면에서 입력해주세요.</p>"
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">"
        msg += ePw
        msg += "</td></tr></tbody></table></div>"
        message.setText(msg, "utf-8", "html") //내용, charset타입, subtype
        message.setFrom(InternetAddress(id, "prac_Admin")) //보내는 사람의 메일 주소, 보내는 사람 이름
        return MessageContainer(ePw, message)
    }

    @Throws(MessagingException::class, UnsupportedEncodingException::class)
    fun createNewPasswordMessage(to: String): MessageContainer {
        val ePw : String = createPassword()
        val message = javaMailSender.createMimeMessage()
        message.addRecipients(MimeMessage.RecipientType.TO, to) // to 보내는 대상
        message.subject = "Working Time 새로운 비밀번호: " //메일 제목

        // 메일 내용 메일의 subtype을 html로 지정하여 html문법 사용 가능
        var msg: String? = ""
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>"
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">새로운 비밀번호입니다. 꼭 비밀번호를 변경해주세요.</p>"
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #F4F4F4; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">"
        msg += ePw
        msg += "</td></tr></tbody></table></div>"
        message.setText(msg, "utf-8", "html") //내용, charset타입, subtype
        message.setFrom(InternetAddress(id, "prac_Admin")) //보내는 사람의 메일 주소, 보내는 사람 이름
        return MessageContainer(ePw, message)
    }

    fun createKey(): String {
        val key = StringBuffer()
        val rnd = Random()
        for (i in 0..5) { // 인증코드 6자리
            key.append(rnd.nextInt(10))
        }
        return key.toString()
    }

    fun createPassword() : String
    {
        var result : String = RandomStringUtils.randomAlphanumeric(24)
        return result
    }

    @Throws(Exception::class)
    fun sendSimpleMessage(to: String): String {
        val messageContainer = createMessage(to)
        try {
            redisUtil.setDataExpire(to, messageContainer.ePw, 60*5L)
            javaMailSender.send(messageContainer.message) // 메일 발송
        } catch (es: MailException) {
            es.printStackTrace()
            throw IllegalArgumentException()
        }
        return messageContainer.ePw // 메일로 보냈던 인증 코드를 서버로 리턴
    }

    @Throws(Exception::class)
    fun sendPasswordMessage(to: String): String {
        val messageContainer = createNewPasswordMessage(to)
        try {
            javaMailSender.send(messageContainer.message) // 메일 발송
        } catch (es: MailException) {
            es.printStackTrace()
            throw IllegalArgumentException()
        }
        return messageContainer.ePw // 메일로 보냈던 인증 코드를 서버로 리턴
    }

    @Throws(ChangeSetPersister.NotFoundException::class)
    fun verifyEmail(key: String?, redeemcode : String?): ResponseDTO {
        val result = ResponseDTO(500, "")
        try {
            val verificationCode : String? = redisUtil.getData(key!!)
            if(verificationCode == null)
            {
                result.code = 1013
                result.description = "verification time expired or no email like this"
            }
            else
            {
                if(verificationCode == redeemcode)
                {
                    result.code = 200
                    result.description = "email verification success"
                    redisUtil.deleteData(key)
                }
                else
                {
                    result.code = 1014
                    result.description = "wrong redeem code"
                }
            }
            return result
        }
        catch (e : ChangeSetPersister.NotFoundException)
        {
            result.code = 1013
            result.description = "verification time expired or no email like this"
            return result
        }
        catch (e : Exception)
        {
            result.code = 500
            result.description = "exception caused"
            return result
        }
    }
}