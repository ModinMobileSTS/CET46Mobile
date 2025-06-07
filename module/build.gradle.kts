plugins {
    id("com.android.application")
}

android {
    namespace = "template.mod"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        release {
            @Suppress("UnstableApiUsage")
            vcsInfo.include = false
        }
    }

    applicationVariants.all {
        val outputFileName = "CET46-$versionName.jar"
        outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = outputFileName
        }
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    compileOnly(fileTree("dir" to "libs", "include" to listOf("*.jar")))
    //noinspection GradleDependency
    compileOnly("com.badlogicgames.gdx:gdx:1.9.10")
    compileOnly("com.badlogicgames.gdx:gdx-freetype:1.9.10")
}
