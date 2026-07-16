# PrintXpress ProGuard rules

# Keep generic signatures and annotations needed by Retrofit/Gson
-keepattributes Signature, Exceptions, InnerClasses, EnclosingMethod, RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, RuntimeVisibleFieldAnnotations

# Keep model classes used by Gson / Retrofit
-keep class com.printxpress.android.data.model.** { *; }

# Keep Retrofit API interfaces
-keep interface com.printxpress.android.data.remote.ApiService { *; }

# Keep all Activities, ViewModels, and Application classes
-keep public class * extends android.app.Activity
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends androidx.lifecycle.ViewModel
-keep public class * extends android.app.Application

# Keep Firebase and Google Play Services classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Gson
-keep class sun.misc.Unsafe { *; }
-dontwarn sun.misc.**

# Keep RecyclerView adapters and view holders
-keep class * extends androidx.recyclerview.widget.RecyclerView$Adapter { *; }
-keep class * extends androidx.recyclerview.widget.RecyclerView$ViewHolder { *; }
