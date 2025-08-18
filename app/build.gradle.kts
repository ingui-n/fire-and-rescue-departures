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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
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
    implementation("io.objectbox:objectbox-android:4.3.0")

    //datastore (náhrada za shared preferences) pro ukládání dat typu klíč hodnota
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("androidx.work:work-runtime-ktx:2.10.2")

    // MapCompose
    implementation("ovh.plrapps:mapcompose:3.1.0")

    implementation(libs.material3)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

//    implementation(libs.proj4j.epsg)
    implementation("org.locationtech.proj4j:proj4j:1.4.1")
    implementation("org.locationtech.proj4j:proj4j-epsg:1.4.1")
    implementation("io.objectbox:objectbox-kotlin:4.3.0")
    kapt("io.objectbox:objectbox-processor:4.3.0")
    implementation("com.composables:icons-lucide-android:1.1.0")

    implementation("org.osmdroid:osmdroid-android:6.1.20")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.37.3")
    implementation("com.jvziyaoyao.scale:image-viewer:1.1.0-alpha.7")
}
