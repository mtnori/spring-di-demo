package com.example.app1

import com.example.logic.SampleRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class AppConfig {

    // Pattern 1: @Primary で優先 Bean を指定 → NoUniqueBeanDefinitionException を解決
    // Pattern 2: @Bean("mainRepository") で @Qualifier("mainRepository") と一致させる
    @Bean("mainRepository")
    @Primary
    fun sampleRepository(): SampleRepository = SampleRepositoryImpl()

    @Bean
    fun sampleRepositoryAlt(): SampleRepository = object : SampleRepository {
        override fun findAll() = listOf("alt-1", "alt-2")
    }
}