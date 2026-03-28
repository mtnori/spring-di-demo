package com.example.app2

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext

@SpringBootTest
class App2ApplicationTests {

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Test
    fun contextLoads() {
    }

    @Test
    fun sampleServiceIsRegisteredDespiteApp2NotUsingIt() {
        // app2 は SampleService を利用しないが、
        // scanBasePackages に com.example.logic が含まれるため Bean 登録される
        assertTrue(
            applicationContext.containsBean("sampleService"),
            "SampleService は app2 で使われていないが、コンポーネントスキャンにより登録されている"
        )
    }
}
