import groovy.lang.GroovyObject
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish`
    idea
    id("com.jfrog.artifactory") version "4.26.1"
    id("com.github.ben-manes.versions") version "0.41.0"
    kotlin("multiplatform") version "1.6.10"
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

val thisGroup = "by.citech"
val thisArtifactId = "transport-layers"
val thisVersion = "4.0.2"

group = thisGroup
version = thisVersion
java.sourceCompatibility = JavaVersion.VERSION_17

val artifactoryUrl: String by project
val artifactoryRepo: String by project
val artifactoryUser: String by project
val artifactoryPass: String by project

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("$artifactoryUrl$artifactoryRepo/")
        credentials {
            username = artifactoryUser
            password = artifactoryPass
        }
    }
}

artifactory {
    setContextUrl(artifactoryUrl)
    publish(delegateClosureOf<PublisherConfig> {
        repository(delegateClosureOf<GroovyObject> {
            setProperty("repoKey", artifactoryRepo)
            setProperty("username", artifactoryUser)
            setProperty("password", artifactoryPass)
            setProperty("maven", true)
        })
        defaults(delegateClosureOf<GroovyObject> {
            invokeMethod("publications", arrayOf("jvm"))
        })
    })
}

kotlin {
    sourceSets {
        jvm {
            withJava()
        }
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

val compileKotlinJvm: KotlinCompile by tasks
compileKotlinJvm.kotlinOptions {
    jvmTarget = java.sourceCompatibility.majorVersion
}

val compileTestKotlinJvm: KotlinCompile by tasks
compileTestKotlinJvm.kotlinOptions {
    jvmTarget = java.sourceCompatibility.majorVersion
}
