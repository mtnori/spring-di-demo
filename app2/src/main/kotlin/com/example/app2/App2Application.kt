package com.example.app2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.common", "com.example.app2"])
class App2Application

fun main(args: Array<String>) {
    runApplication<App2Application>(*args)
}