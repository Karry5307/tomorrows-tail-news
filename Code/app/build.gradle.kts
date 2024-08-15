import com.android.build.api.dsl.Packaging
import com.android.utils.TraceUtils.simpleId

plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.java.tomorrowstailnews"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.java.tomorrowstailnews"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.gson)
    implementation(libs.glide)
    implementation(libs.okhttp)
    implementation(libs.refresh.layout.kernel)
    implementation(libs.refresh.header.classics)
    implementation(libs.refresh.header.radar)
    implementation(libs.refresh.header.falsify)
    implementation(libs.refresh.header.material)
    implementation(libs.refresh.header.two.level)
    implementation(libs.io.github.scwang90.refresh.footer.ball4)
    implementation(libs.io.github.scwang90.refresh.footer.classics4)
    implementation(libs.oapi.java.sdk)
}