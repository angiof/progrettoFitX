# Core keeps
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Room
-keep @androidx.room.Dao class *
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.LimitOffsetDataSource

# Compose
-keep class androidx.compose.runtime.ComposerImpl { *; }
-keep class androidx.compose.runtime.ComposerKt { *; }
-dontwarn androidx.compose.**

# Gson
-dontwarn com.google.gson.**
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep class com.app.fityo.data_layer.db.converters.** { *; }

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OpenFoodFacts API Models (Retrofit + Gson)
-keep class com.app.fityo.data_layer.network.OpenFoodFactsProductResponse { *; }
-keep class com.app.fityo.data_layer.network.OpenFoodFactsSearchResponse { *; }
-keep class com.app.fityo.data_layer.network.OpenFoodFactsProduct { *; }
-keep class com.app.fityo.data_layer.network.OpenFoodFactsNutriments { *; }
-keep class com.app.fityo.data_layer.network.** { *; }
-keepclassmembers class com.app.fityo.data_layer.network.** {
    <init>(...);
    <fields>;
}

# Kotlinx metadata (used by Room)
-dontwarn kotlinx.metadata.**

# MediaPipe
-keep class com.google.mediapipe.** { *; }
-dontwarn com.google.mediapipe.**
-keep class autovalue.shaded.** { *; }
-dontwarn autovalue.shaded.**
-dontwarn com.google.auto.value.**

# Missing javax classes (used by MediaPipe internally)
-dontwarn javax.lang.model.**
-dontwarn javax.annotation.processing.**

# Keep MediaPipe tasks
-keep class com.google.mediapipe.tasks.** { *; }
-keep class com.google.mediapipe.framework.** { *; }

# TensorFlow Lite
-keep class org.tensorflow.lite.** { *; }
-dontwarn org.tensorflow.lite.**
