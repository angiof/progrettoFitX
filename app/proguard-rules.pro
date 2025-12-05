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

# Kotlinx metadata (used by Room)
-dontwarn kotlinx.metadata.**
