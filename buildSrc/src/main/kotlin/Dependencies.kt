object DependencyVersions {
    const val buildTools = "29.0.2"
    const val compileSdk = 29
    const val minSdk = 21
    const val targetSdk = 29

    const val kotlin = "1.3.61"
    const val gradle = "3.5.2"


    const val androidx = "1.0.0"
    const val appCompat = "1.0.2"
    const val lifecycle = "2.1.0"
    const val androidKtx = "1.0.1"
    const val recyclerView = "28.0.0'"
    const val constraintLayout = "1.1.3"

    const val rxJava = "2.2.12"
    const val rxAndroid = "2.1.1"
    const val rxKotlin = "2.4.0"

    const val room = "2.2.3"
    const val okHttp = "4.2.2"
    const val dagger = "2.25.2"
    const val retrofit = "2.6.2"
    const val gson = "2.8.6"
    const val findBugs = "jsr305:3.0.2"

}

object GradlePluginVersions {
    const val android = "com.android.tools.build:gradle:${DependencyVersions.gradle}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${DependencyVersions.kotlin}"
}

object LibraryVersions {
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${DependencyVersions.kotlin}"
    const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${DependencyVersions.kotlin}"

    const val rxJava = "io.reactivex.rxjava2:rxjava:${DependencyVersions.rxJava}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${DependencyVersions.rxAndroid}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${DependencyVersions.rxKotlin}"

    const val appCompat = "androidx.appcompat:appcompat:${DependencyVersions.appCompat}"
    const val recyclerView =
        "com.android.support:recyclerview-v7:${DependencyVersions.recyclerView}"

    const val lifecycle =
        "androidx.lifecycle:lifecycle-extensions:${DependencyVersions.lifecycle}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${DependencyVersions.constraintLayout}"
    const val androidKtx = "androidx.core:core-ktx:${DependencyVersions.androidKtx}"

    const val room = "androidx.room:room-runtime:${DependencyVersions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${DependencyVersions.room}"
    const val roomRx = "androidx.room:room-rxjava2:${DependencyVersions.room}"

    const val okHttp = "com.squareup.okhttp3:okhttp:${DependencyVersions.okHttp}"
    const val okHttpLogging =
        "com.squareup.okhttp3:logging-interceptor:${DependencyVersions.okHttp}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${DependencyVersions.retrofit}"
    const val gson = "com.google.code.gson:gson:${DependencyVersions.gson}"
    const val gsonConverter = "com.squareup.retrofit2:converter-gson:${DependencyVersions.retrofit}"
    const val rxConverter = "com.squareup.retrofit2:adapter-rxjava2:${DependencyVersions.retrofit}"

    const val dagger = "com.google.dagger:dagger:${DependencyVersions.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${DependencyVersions.dagger}"
    const val findBugs = "com.google.code.findbugs:${DependencyVersions.findBugs}"

}
