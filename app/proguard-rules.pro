# TaskKu R8 and ProGuard Optimization Rules

# AndroidX Compose
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Kotlin Coroutines
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Kotlinx Serialization
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

# Room Database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**

# Glance AppWidget
-keep class androidx.glance.** { *; }
-dontwarn androidx.glance.**

# Keep models and entities
-keep class com.example.taskku.domain.model.** { *; }
-keep class com.example.taskku.data.local.entity.** { *; }
