package com.workingtime.rank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class RankApplication

fun main(args: Array<String>) {
    runApplication<RankApplication>(*args)
}