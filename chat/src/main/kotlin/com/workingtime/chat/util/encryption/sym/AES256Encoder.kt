package com.workingtime.chat.util.encryption.sym

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class AES256Encoder {

    @Value("\${encrypt.aes256.key}")
    private var key : String = ""

    private val alg = "AES/CBC/PKCS5Padding"

    @Throws(Exception::class)
    fun encrypt(text: String): String {
        val iv : String = key.substring(0, 16) // 16byte
        val cipher = Cipher.getInstance(alg)
        val keySpec = SecretKeySpec(iv.toByteArray(), "AES")
        val ivParamSpec = IvParameterSpec(iv.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec)
        val encrypted = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(encrypted)
    }

    @Throws(Exception::class)
    fun decrypt(cipherText: String?): String {
        val iv : String = key.substring(0, 16) // 16byte
        val cipher = Cipher.getInstance(alg)
        val keySpec = SecretKeySpec(iv.toByteArray(), "AES")
        val ivParamSpec = IvParameterSpec(iv.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec)
        val decodedBytes = Base64.getDecoder().decode(cipherText)
        val decrypted = cipher.doFinal(decodedBytes)
        return decrypted.toString(Charsets.UTF_8)
    }
}