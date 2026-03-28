package com.example.app2

import com.example.common.SampleService
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class App2ApplicationTests {

    @Autowired
    lateinit var sampleService: SampleService

    @Test
    fun contextLoads() {
    }

    @Test
    fun repositoryIsAbsentAndReturnsEmpty() {
        assertNull(sampleService.repository, "app2 has no Repository bean, should be null")
        assertTrue(sampleService.getItems().isEmpty(), "getItems() should return empty list")
    }
}