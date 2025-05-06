// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) version "2.1.0" apply false

    // add KSP plugin
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}