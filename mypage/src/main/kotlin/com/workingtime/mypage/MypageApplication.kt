package com.workingtime.mypage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class MypageApplication

fun main(args: Array<String>) {
	runApplication<MypageApplication>(*args)
}
