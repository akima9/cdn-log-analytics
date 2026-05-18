plugins {
    id("org.springframework.boot")
    kotlin("plugin.jpa")
}

dependencies {
    // Core 모듈
    implementation(project(":core"))

    // Batch
    implementation("org.springframework.boot:spring-boot-starter-batch")

    // JPA (core의 Repository 타입 사용, JpaItemWriter 의존)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Test
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("com.h2database:h2")
}
