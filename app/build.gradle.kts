import org.gradle.kotlin.dsl.implementation
import java.util.Properties
import java.io.File

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

val localProperties = Properties()
val localPropertiesFile = File(rootProject.projectDir, "local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace = "com.example.newsappliaction"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.newsappliaction"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "NEWS_API_KEY", "\"${localProperties.getProperty("NEWS_API_KEY", "")}\"")
        buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("BASE_URL", "")}\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    configurations.all {
        resolutionStrategy {
            eachDependency {
                if (requested.group == "org.jetbrains.kotlin") {
                    useVersion("1.9.21")
                    because("Align with KSP version")
                }
                // Force Room to use compatible version
                if (requested.group == "androidx.room") {
                    useVersion("2.6.1")
                    because("Use Room version compatible with Kotlin 1.9.21")
                }
            }
            // Force resolving specific dependencies
            force("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.21")
        }
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)


    implementation(libs.espresso.idling.resource)
    implementation(libs.retrofit2.converter.scalars)
    implementation(libs.retrofit2.converter.jackson)
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)


    // Retrofit
    implementation(libs.converter.gson)
    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp3.logging.interceptor)

    // Glide
    implementation(libs.glide)
    kapt(libs.compiler)

    // Coroutines
    implementation (libs.jetbrains.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)

    //  Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)





}