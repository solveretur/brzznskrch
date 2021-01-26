import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
	kotlin("jvm") version "1.4.21"
	kotlin("plugin.spring") version "1.4.21"
	jacoco
	id("io.gitlab.arturbosch.detekt") version "1.15.0"
}

group = "com.brzezinski"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	jcenter()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("io.github.microutils:kotlin-logging:2.0.4")
	implementation("com.squareup.retrofit2:retrofit:2.9.0")
	implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")
	implementation("com.jakewharton.retrofit:retrofit2-reactor-adapter:2.1.0")
	implementation("commons-validator:commons-validator:1.7")
	implementation("com.squareup.okhttp3:logging-interceptor:3.14.9")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging {
		events  = setOf(
			org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
			org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
			org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
		)
	}
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.isEnabled = false
		csv.isEnabled = false
		html.isEnabled = true
		html.destination = file("${buildDir}/jacocoHtml")
	}
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = 0.9.toBigDecimal()
			}
		}
	}
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
	filter {
		exclude("**/*.kts")
	}
}

detekt {
	baseline = file("src/main/resources/static/detekt/baseline.xml")
	reports {
		xml {
			enabled = false
		}
		txt {
			enabled = false
		}
	}
}