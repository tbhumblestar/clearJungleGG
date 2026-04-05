plugins {
	kotlin("jvm") version "2.3.20"
	kotlin("plugin.spring") version "2.3.20"
	id("org.springframework.boot") version "4.0.5"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "2.3.20"
}

group = "gg.jungleclear"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-cache")

	// Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")

	// Coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

	// DB
	runtimeOnly("org.postgresql:postgresql")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

// .env 파일에서 환경변수 로드
fun loadDotenv(): Map<String, String> {
	val envFile = rootProject.file("backend.env")
	if (!envFile.exists()) return emptyMap()
	return envFile.readLines()
		.filter { it.isNotBlank() && !it.startsWith("#") && it.contains("=") }
		.associate { line ->
			val (key, value) = line.split("=", limit = 2)
			key.trim() to value.trim()
		}
}

tasks.withType<Test> {
	useJUnitPlatform()
	environment(loadDotenv())
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	environment(loadDotenv())
}
