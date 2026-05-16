import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

// Leer API_BASE_URL desde local.properties (override local) o gradle.properties (equipo)
val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties().apply {
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.inputStream())
    }
}

val apiBaseUrl = (localProperties.getProperty("API_BASE_URL")
    ?: project.findProperty("API_BASE_URL") as String?)
    ?: "http://10.0.2.2:3010/api/v1/"

val backendBaseUrl = (localProperties.getProperty("BACKEND_BASE_URL")?.trim()?.removeSurrounding("\"")
    ?: project.findProperty("BACKEND_BASE_URL") as String?)
    ?: "http://10.0.2.2:3010/"

val googleWebClientId = (localProperties.getProperty("GOOGLE_WEB_CLIENT_ID")
    ?: project.findProperty("GOOGLE_WEB_CLIENT_ID") as String?)
    ?: ""

val facebookAppId = (localProperties.getProperty("FACEBOOK_APP_ID")
    ?: project.findProperty("FACEBOOK_APP_ID") as String?)
    ?: ""

val facebookClientToken = (localProperties.getProperty("FACEBOOK_CLIENT_TOKEN")
    ?: project.findProperty("FACEBOOK_CLIENT_TOKEN") as String?)
    ?: ""

android {
    namespace = "edu.gymtonic_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "edu.gymtonic_app"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("String", "BACKEND_BASE_URL", "\"$backendBaseUrl\"")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
        buildConfigField("String", "FACEBOOK_APP_ID", "\"$facebookAppId\"")
        resValue("string", "facebook_app_id", facebookAppId)
        resValue("string", "facebook_client_token", facebookClientToken)
        resValue("string", "fb_login_protocol_scheme", "fb$facebookAppId")

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui.text)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    //Iconos
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // ROOM dependencies
    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2") // Soporte para Coroutines y Kotlin Extensions.
    ksp("androidx.room:room-compiler:2.7.2") // KSP para procesamiento de anotaciones.

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.9.3")
    // Retrofit2
    implementation("com.squareup.retrofit2:retrofit:3.0.0")

    // Conversor para JSON (Gson)
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // Corutinas (para llamadas asíncronas)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // DataStore Preferences
    implementation(libs.androidx.datastore.preferences)

    // Coil para carga de imágenes desde URL
    implementation(libs.coil.compose)

    // Media3 para reproducción de vídeo (ExoPlayer)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)

    // Google Sign-In with Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.facebook.login)
}
