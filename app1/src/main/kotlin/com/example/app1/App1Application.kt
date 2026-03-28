package com.example.app1

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.example.logic", "com.example.app1"])
class App1Application

fun main(args: Array<String>) {
    runApplication<App1Application>(*args)
}