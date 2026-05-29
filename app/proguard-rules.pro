# Keep Retrofit interfaces
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclassmembernames interface * {
    @retrofit2.http.* <methods>;
}

# Keep Gson model
-keep class com.example.cryptotracker.model.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
