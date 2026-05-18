plugins {
    id("org.springframework.boot")
    kotlin("plugin.jpa")
}

dependencies {
    // Core 모듈
    implementation(project(":core"))

    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Security + JWT
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    // Test
    testImplementation("org.springframework.security:spring-security-test")
}
