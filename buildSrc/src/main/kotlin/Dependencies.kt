object DepVers {
    const val buildTools = "29.0.2"
    const val compileSdk = 29
    const val minSdk = 21
    const val targetSdk = 29

    const val kotlin = "1.3.61"
    const val gradle = "3.5.2"


    const val androidx = "1.0.0"
    const val appCompat = "1.0.2"
    const val lifecycle = "2.1.0"
    const val material = "1.0.0"
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
    const val stetho = "1.5.1"
    const val junit = "4.12"

}

object GradlePluginVersions {
    const val android = "com.android.tools.build:gradle:${DepVers.gradle}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${DepVers.kotlin}"
}

object LibraryVersions {
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${DepVers.kotlin}"

    const val rxJava = "io.reactivex.rxjava2:rxjava:${DepVers.rxJava}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${DepVers.rxAndroid}"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:${DepVers.rxKotlin}"

    const val appCompat = "androidx.appcompat:appcompat:${DepVers.appCompat}"
    const val recyclerView = "com.android.support:recyclerview-v7:${DepVers.recyclerView}"

    const val lifecycle = "androidx.lifecycle:lifecycle-extensions:${DepVers.lifecycle}"

    const val material = "com.google.android.material:material:${DepVers.material}"

    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${DepVers.constraintLayout}"

    const val androidKtx = "androidx.core:core-ktx:${DepVers.androidKtx}"

    const val room = "androidx.room:room-runtime:${DepVers.room}"
    const val roomCompiler = "androidx.room:room-compiler:${DepVers.room}"
    const val roomRx = "androidx.room:room-rxjava2:${DepVers.room}"

    const val okHttp = "com.squareup.okhttp3:okhttp:${DepVers.okHttp}"
    const val okHttpLogging = "com.squareup.okhttp3:logging-interceptor:${DepVers.okHttp}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${DepVers.retrofit}"
    const val gson = "com.google.code.gson:gson:${DepVers.gson}"
    const val gsonConverter = "com.squareup.retrofit2:converter-gson:${DepVers.retrofit}"
    const val rxConverter = "com.squareup.retrofit2:adapter-rxjava2:${DepVers.retrofit}"

    const val dagger = "com.google.dagger:dagger:${DepVers.dagger}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${DepVers.dagger}"
    const val findBugs = "com.google.code.findbugs:${DepVers.findBugs}"

    const val stetho = "com.facebook.stetho:stetho:${DepVers.stetho}"
    const val junit = "junit:junit:${DepVers.junit}"

}
