package com.brzezinski.roche

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RocheApplication

fun main(args: Array<String>) {
    runApplication<RocheApplication>(*args)
}
