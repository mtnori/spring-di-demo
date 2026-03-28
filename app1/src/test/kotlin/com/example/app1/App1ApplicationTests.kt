package com.example.app1

import com.example.common.SampleService
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class App1ApplicationTests {

    @Autowired
    lateinit var sampleService: SampleService

    @Test
    fun contextLoads() {
    }

    @Test
    fun repositoryIsInjected() {
        assertNotNull(sampleService.repository, "Repository should be injected in app1")
        println("Items: ${sampleService.getItems()}")
    }
}