import java.net.URL

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.nv"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-crypto")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val fetchOpenApiSpec by tasks.registering {
	group = "documentation"
	description = "Fetch OpenAPI spec from the running app and copy to static folder"

	doLast {
		val outputFile = file("src/main/resources/static/openapi.json")
		val url = URL("http://localhost:8080/v3/api-docs")

		println("➡️ Waiting for server to start...")
		val maxAttempts = 20
		val delayMillis = 1000L

		var success = false
		repeat(maxAttempts) { attempt ->
			try {
				val json = url.readText()
				outputFile.parentFile.mkdirs()
				outputFile.writeText(json)
				println("Swagger spec written to ${outputFile.absolutePath}")
				success = true
				return@repeat
			} catch (e: Exception) {
				println("Attempt ${attempt + 1}/$maxAttempts failed: ${e.message}")
				Thread.sleep(delayMillis)
			}
		}

		if (!success) {
			throw GradleException("Failed to fetch OpenAPI spec after $maxAttempts attempts.")
		}
	}
}

tasks.named("build") {
	dependsOn("fetchOpenApiSpec")
}