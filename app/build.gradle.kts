import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("io.objectbox")
}

android {
    namespace = "com.android.fire_and_rescue_departures"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.android.fire_and_rescue_departures"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val localProperties = gradleLocalProperties(rootDir, providers)
            buildConfigField("String", "MAPS_COM_API", localProperties.getProperty("MAPS_COM_API"))
            buildConfigField("String", "OPEN_STREET_MAP_API_URL", localProperties.getProperty("OPEN_STREET_MAP_API_URL"))
            buildConfigField("String", "REPORT_INTERVAL_MINUTES", localProperties.getProperty("REPORT_INTERVAL_MINUTES"))
        }
        debug {
            val localProperties = gradleLocalProperties(rootDir, providers)
            buildConfigField("String", "MAPS_COM_API", localProperties.getProperty("MAPS_COM_API"))
            buildConfigField("String", "OPEN_STREET_MAP_API_URL", localProperties.getProperty("OPEN_STREET_MAP_API_URL"))
            buildConfigField("String", "REPORT_INTERVAL_MINUTES", localProperties.getProperty("REPORT_INTERVAL_MINUTES"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.generativeai)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //koin (pro dependency injection)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // OkHttp (pro logování)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //objectbox (nosql) pro datovou persistenci (vektorová DB)
    implementation(libs.objectbox.android)

    //datastore (náhrada za shared preferences) pro ukládání dat typu klíč hodnota
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)
    implementation(libs.androidx.work.runtime.ktx)

    // MapCompose
    implementation(libs.mapcompose)

    implementation(libs.material3)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

//    implementation(libs.proj4j.epsg)
    implementation(libs.proj4j)
    implementation(libs.proj4j.epsg)
    implementation(libs.objectbox.kotlin)
    kapt(libs.objectbox.processor)
    implementation(libs.icons.lucide.android)

    implementation(libs.osmdroid.android)
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.image.viewer)
}
