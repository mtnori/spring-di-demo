package com.example.common

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean

// @AutoConfiguration を使うことで、ユーザーの @Configuration より後に処理される
// → @ConditionalOnMissingBean が正しく評価される
@AutoConfiguration
class DefaultRepositoryConfig {

    // Pattern 3: SampleRepository Bean が存在しない場合のデフォルト no-op 実装
    // - Bean 名を "mainRepository" にすることで @Qualifier("mainRepository") にも対応
    // - app1/app2 が実装を提供すれば、この Bean は作成されない
    @Bean("mainRepository")
    @ConditionalOnMissingBean(SampleRepository::class)
    fun noOpSampleRepository(): SampleRepository = object : SampleRepository {
        override fun findAll(): List<String> = emptyList()
    }
}