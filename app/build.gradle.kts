plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.gms.google-services")
    // Maps SDK for Android
  id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

secrets {
  // Optionally specify a different file name containing your secrets.
  // The plugin defaults to "local.properties"
  propertiesFileName = "secrets.properties"

  // A properties file containing default secret values. This file can be
  // checked in version control.
  defaultPropertiesFileName = "local.defaults.properties"

  // Configure which keys should be ignored by the plugin by providing regular expressions.
  // "sdk.dir" is ignored by default.
  ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
  ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}

android {
  namespace = "com.example.learnsphere"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.learnsphere"
    minSdk = 28
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  // Testing configurations
  testOptions {
    unitTests {
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.1"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}


dependencies {
  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
  implementation("androidx.activity:activity-compose:1.8.2")
  implementation(platform("androidx.compose:compose-bom:2023.08.00"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
  implementation("androidx.navigation:navigation-compose:2.7.7")
  implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
  implementation("com.google.firebase:firebase-storage:20.3.0")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
  debugImplementation("androidx.compose.ui:ui-tooling")
  debugImplementation("androidx.compose.ui:ui-test-manifest")

  //  Google Map
  implementation("com.google.android.gms:play-services-maps:18.2.0")

  // Android Maps Compose composables for the Maps SDK for Android
  implementation("com.google.maps.android:maps-compose:4.3.0")


  implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
  implementation("com.google.firebase:firebase-analytics")
  implementation("com.google.firebase:firebase-firestore")

//  For QR Code
  implementation("io.github.g00fy2.quickie:quickie-bundled:1.8.0")
  implementation("com.google.zxing:core:3.4.0")

  implementation("androidx.compose.material:material-icons-extended")
//  For Json
  implementation("com.google.code.gson:gson:2.8.8")

// For Image Upload/Retrieval
  implementation("com.github.bumptech.glide:glide:4.16.0")

  //Coil
  implementation ("io.coil-kt:coil-compose:2.4.0")

  // For location
  implementation("com.google.android.gms:play-services-location:21.1.0")

  implementation("com.kizitonwose.calendar:compose:2.5.0")

  implementation("androidx.core:core-splashscreen:1.0.0")

  implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.5.0")

  // Test dependencies
  testImplementation ("junit:junit:4.13.2") // JUnit
  testImplementation ("org.mockito:mockito-core:3.12.4") // Mockito

}