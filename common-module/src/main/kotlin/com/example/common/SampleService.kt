package com.example.common

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class SampleService {

    // Pattern 2: "mainRepository" という名前の Bean のみ受け付ける
    // → app2 の SampleRepositoryImpl2 (Bean名: "sampleRepositoryImpl2") は注入されない
    @Autowired(required = false)
    @Qualifier("mainRepository")
    var repository: SampleRepository? = null

    fun getItems(): List<String> = repository?.findAll() ?: emptyList()
}