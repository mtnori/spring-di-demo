package com.example.app2

import com.example.common.SampleRepository
import org.springframework.stereotype.Repository

@Repository
class SampleRepositoryImpl2 : SampleRepository {
    override fun findAll(): List<String> = listOf("item-x", "item-y")
}