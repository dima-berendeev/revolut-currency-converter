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
    compileSdkVersion(DepVers.compileSdk)
    buildToolsVersion(DepVers.buildTools)

    defaultConfig {
        minSdkVersion(DepVers.minSdk)
        targetSdkVersion(DepVers.targetSdk)
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
    implementation(LibraryVersions.material)

    //-------------------network---------------
    implementation(LibraryVersions.okHttpLogging)
    implementation(LibraryVersions.okHttp)
    implementation(LibraryVersions.gson)
    implementation(LibraryVersions.retrofit)
    implementation(LibraryVersions.gsonConverter)
    implementation(LibraryVersions.rxConverter)
    implementation(LibraryVersions.glide)
    kapt(LibraryVersions.glideCompiler)

    //-------------------persistence-------------------
    implementation(LibraryVersions.room)
    implementation(LibraryVersions.roomRx)
    kapt(LibraryVersions.roomCompiler)

    //-------------------di-------------------
    implementation(LibraryVersions.dagger)
    implementation(LibraryVersions.findBugs)
    kapt(LibraryVersions.daggerCompiler)

    //-------------------develop-utils-------------------
    implementation(LibraryVersions.stetho)
    testImplementation(LibraryVersions.junit)
}
