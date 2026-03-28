package com.example.app1

import com.example.common.SampleRepository

class SampleRepositoryImpl : SampleRepository {
    override fun findAll(): List<String> = listOf("item-a", "item-b", "item-c")
}