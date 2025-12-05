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

# Kotlinx metadata (used by Room)
-dontwarn kotlinx.metadata.**
