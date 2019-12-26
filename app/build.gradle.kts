plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

extensions.getByType(org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension::class.java).run {
    isExperimental = true
}

android {
    compileSdkVersion(DependencyVersions.compileSdk)
    buildToolsVersion(DependencyVersions.buildTools)

    defaultConfig {
        minSdkVersion(DependencyVersions.minSdk)
        targetSdkVersion(DependencyVersions.targetSdk)
        versionCode = Versions.versionCode
        versionName = Versions.versionName
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    buildTypes {
        getByName("debug") {
            versionNameSuffix = ".dev"
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        create("beta") {
            versionNameSuffix = ".dev"
            applicationIdSuffix = ".test"
            isDebuggable = true

            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            isShrinkResources = true
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            isShrinkResources = true
        }
    }


}


dependencies {
    //-------------------core------------------
    implementation(LibraryVersions.kotlinStdLib)
    implementation(LibraryVersions.rxJava)
    implementation(LibraryVersions.rxAndroid)
    implementation(LibraryVersions.rxKotlin)

    //-------------------android---------------
    implementation(LibraryVersions.appCompat)
    implementation(LibraryVersions.recyclerView)
    implementation(LibraryVersions.androidKtx)
    implementation(LibraryVersions.constraintLayout)
    implementation(LibraryVersions.lifecycle)
    implementation("com.google.android.material:material:1.0.0")


    //-------------------network---------------
    implementation(LibraryVersions.okHttpLogging)
    implementation(LibraryVersions.okHttp)
    implementation(LibraryVersions.gson)
    implementation(LibraryVersions.gsonConverter)
    implementation(LibraryVersions.rxConverter)
    implementation("com.squareup.picasso:picasso:2.71828")

    //-------------------persistence-------------------
    implementation(LibraryVersions.room)
    implementation(LibraryVersions.roomRx)
    kapt(LibraryVersions.roomCompiler)

    //-------------------di-------------------
    implementation(LibraryVersions.dagger)
    implementation(LibraryVersions.findBugs)
    kapt(LibraryVersions.daggerCompiler)

    //-------------------develop-utils-------------------
    implementation("com.facebook.stetho:stetho:1.5.1")
    testImplementation("junit:junit:4.12")
}
