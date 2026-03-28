# spring-di-demo

Spring Boot + Kotlin のマルチプロジェクト構成で、DI（依存性注入）の挙動と3つの制御パターンを学ぶためのデモプロジェクトです。

## プロジェクト構成

```
spring-di-demo/
├── common-module/   共通ライブラリ（SampleRepository インターフェース + SampleService）
├── app1/            SampleRepository の実装を提供するアプリ
└── app2/            別の Repository 実装を持つアプリ
```

### 依存関係

```
app1 ──┐
       ├──> common-module
app2 ──┘
```

- `common-module` は Spring Boot plugin を持たない通常の library JAR
- `app1` / `app2` は Spring Boot アプリ

---

## common-module の設計

### SampleRepository（インターフェース）

```kotlin
interface SampleRepository {
    fun findAll(): List<String>
}
```

### SampleService

```kotlin
@Service
class SampleService {

    @Autowired(required = false)
    @Qualifier("mainRepository")
    var repository: SampleRepository? = null

    fun getItems(): List<String> = repository?.findAll() ?: emptyList()
}
```

- `required = false` : `SampleRepository` Bean が存在しない場合も DI エラーにならない
- `@Qualifier("mainRepository")` : Bean 名が `"mainRepository"` のものだけを注入対象にする

---

## 発生した問題と3つの解決パターン

### 前提：意図せず挙動が変わるケース

`app2` に `@Repository` アノテーション付きの `SampleRepositoryImpl2` を追加すると、コンポーネントスキャンによって自動的に Bean 登録される。
`required = false` は「Bean がなければ null にする」という意味であり、「注入しない」ではないため、`SampleService.repository` に予期せず `SampleRepositoryImpl2` が注入されてしまう。

```
// app2 に追加しただけで common-module の SampleService の挙動が変わる
@Repository
class SampleRepositoryImpl2 : SampleRepository {
    override fun findAll() = listOf("item-x", "item-y")
}
```

---

### Pattern 1：`@Primary` — 複数 Bean の優先順位を明示する

**問題：** `AppConfig` に同じ型の Bean が2つ登録されると `NoUniqueBeanDefinitionException` が発生する。

```
Caused by: org.springframework.beans.factory.NoUniqueBeanDefinitionException
```

**解決：** 優先させたい Bean に `@Primary` を付与する。

```kotlin
// app1/AppConfig.kt
@Configuration
class AppConfig {

    @Bean("mainRepository")
    @Primary                          // <-- この Bean を優先
    fun sampleRepository(): SampleRepository = SampleRepositoryImpl()

    @Bean
    fun sampleRepositoryAlt(): SampleRepository = object : SampleRepository {
        override fun findAll() = listOf("alt-1", "alt-2")
    }
}
```

**効果：** `@Autowired` で型解決が曖昧な場合に `@Primary` が付いた Bean が選ばれる。

---

### Pattern 2：`@Qualifier` — 注入する Bean を名前で限定する

**問題：** `app2` に `SampleRepositoryImpl2` を追加すると、`SampleService.repository` に意図せず注入される。

**解決：** `SampleService` 側で `@Qualifier("mainRepository")` を指定し、Bean 名が一致するものだけ注入対象にする。
`AppConfig` の `@Bean("mainRepository")` と対応させることで、名前が異なる Bean（例：`"sampleRepositoryImpl2"`）は注入されない。

```kotlin
// common-module/SampleService.kt
@Autowired(required = false)
@Qualifier("mainRepository")         // <-- "mainRepository" のみ受け付ける
var repository: SampleRepository? = null
```

```kotlin
// app1/AppConfig.kt
@Bean("mainRepository")              // <-- Qualifier と名前を一致させる
@Primary
fun sampleRepository(): SampleRepository = SampleRepositoryImpl()
```

**効果：** `app2` に `@Repository` な実装があっても、Bean 名が `"mainRepository"` でなければ注入されない。
`app2` の `SampleService.repository` は `null` のままとなり、`getItems()` は空リストを返す。

---

### Pattern 3：`@ConditionalOnMissingBean` — デフォルト実装をライブラリ側で提供する

**問題：** 誰も `SampleRepository` を提供しないとき、`required = false` で `null` になり、
呼び出し側で常に null チェックが必要になる。

**解決：** `common-module` 側でデフォルトの no-op 実装を提供する。アプリが独自の実装を提供すればそちらが使われる。

```kotlin
// common-module/DefaultRepositoryConfig.kt
@AutoConfiguration
class DefaultRepositoryConfig {

    @Bean("mainRepository")
    @ConditionalOnMissingBean(SampleRepository::class)
    fun noOpSampleRepository(): SampleRepository = object : SampleRepository {
        override fun findAll(): List<String> = emptyList()
    }
}
```

**`@AutoConfiguration` が必要な理由：**
通常の `@Configuration` はユーザー定義の `@Configuration` と同じタイミングで処理されるため、
`AppConfig` と同名 Bean を競合登録して `BeanDefinitionOverrideException` が発生する。
`@AutoConfiguration` + `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` への登録により、
ユーザー設定がすべて確定した後に条件が評価されることが保証される。

```
# common-module/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
com.example.common.DefaultRepositoryConfig
```

**条件の評価結果：**

| 状況 | `@ConditionalOnMissingBean` の結果 | `SampleService.repository` |
|------|------------------------------------|----------------------------|
| 誰も `SampleRepository` を提供しない | NoOp Bean を作成 | NoOp が注入される（空リスト） |
| `app1` が `"mainRepository"` Bean を提供 | NoOp Bean を作成しない | `SampleRepositoryImpl` が注入される |
| `app2` が `@Repository` な実装を持つ | NoOp Bean を作成しない（型が存在するため） | `@Qualifier` によりその実装は注入されず `null` |

---

## テスト実行

```bash
# common-module のコンパイル確認
./gradlew :common-module:compileKotlin

# app1: SampleRepositoryImpl が注入されることを確認
./gradlew :app1:test

# app2: DI エラーなし・getItems() が空リストを返すことを確認
./gradlew :app2:test

# 全テスト
./gradlew test
```

## 起動

```bash
./gradlew :app1:bootRun
./gradlew :app2:bootRun
```

---

## 技術スタック

| | バージョン |
|---|---|
| Spring Boot | 4.0.5 |
| Kotlin | 2.2.21 |
| Gradle | 9.4.1 |
| Java | 17 |
