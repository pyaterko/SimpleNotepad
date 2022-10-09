import org.gradle.api.JavaVersion

object Config {
    const val application_id =
        "com.owl_laugh_at_wasted_time.simplenotepad"
    const val compile_sdk = 32
    const val min_sdk = 24
    const val target_sdk = 32
    val java_version = JavaVersion.VERSION_1_8
}

object Releases {
    const val version_code = 1
    const val version_name = "1.0"
}

object Modules {
    const val app = ":app"
    const val domain = ":domain"
    const val data = ":data"
    const val viewmodel = ":viewmodel"
}

object Versions {

    //Fragment
    const val fragment = "1.5.0"
    const val navigationFragmentKtx = "2.5.0"
    const val navigationUiKtx = "2.5.0"
    const val navigationDynamicFeatures = "2.5.0"

    //Design
    const val appcompat = "1.4.2"
    const val material = "1.6.1"

    //Kotlin
    const val core = "1.8.0"
    const val stdlib = "1.5.21"
    const val coroutinesCore = "1.6.1"
    const val coroutinesAndroid = "1.6.1"

    //Dagger
    const val dagger = "2.40"
    const val daggerCompiler = "2.40"

    //Room
    const val roomKtx = "2.4.2"
    const val runtime = "2.4.2"
    const val roomCompiler = "2.4.2"

}

object Fragment {
    const val fragmentktx = "androidx.fragment:fragment-ktx:${Versions.fragment}"
    const val navigation_fragment_ktx =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigationFragmentKtx}"
    const val navigation_ui_ktx =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigationUiKtx}"
    const val navigation_dinamic_fragment =
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigationDynamicFeatures}"
}

object Design {
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val material = "com.google.android.material:material:${Versions.material}"
}

object Kotlin {
    const val core = "androidx.core:core-ktx:${Versions.core}"
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.stdlib}"
    const val coroutines_core =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesCore}"
    const val coroutines_android =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesAndroid}"
}

object Dagger {
    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val dagger_compiler =
        "com.google.dagger:dagger-compiler:${Versions.daggerCompiler}"
}

object Room {
    const val runtime = "androidx.room:room-runtime:${Versions.runtime}"
    const val compiler = "androidx.room:room-compiler:${Versions.roomCompiler}"
    const val room_ktx = "androidx.room:room-ktx:${Versions.roomKtx}"
}








