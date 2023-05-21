package com.workingtime.check

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class CheckApplication

fun main(args: Array<String>) {
	runApplication<CheckApplication>(*args)
}
