# ProGuard Rules for Phone Repair Shop App

# Keep Application class
-keep class com.phonerepair.shop.PhoneRepairApplication { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp { *; }

# Keep Room database classes
-keep class com.phonerepair.shop.data.local.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }

# Keep Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }
-keep @kotlinx.serialization.Serializable class * { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-keep class androidx.activity.compose.** { *; }
-keep class androidx.lifecycle.viewmodel.compose.** { *; }

# Keep Navigation
-keep class androidx.navigation.** { *; }

# Keep Coil
-keep class coil.** { *; }

# Keep MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# Keep iText
-keep class com.itextpdf.** { *; }

# Keep ML Kit
-keep class com.google.mlkit.** { *; }

# Keep CameraX
-keep class androidx.camera.** { *; }

# Keep WorkManager
-keep class androidx.work.** { *; }

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep Timber
-keep class timber.log.** { *; }

# Keep Threetenbp
-keep class org.threeten.** { *; }

# Keep model classes
-keep class com.phonerepair.shop.data.model.** { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel { *; }

# Keep Repository interfaces and implementations
-keep class com.phonerepair.shop.data.repository.** { *; }

# Keep UI components
-keep class com.phonerepair.shop.ui.** { *; }

# Keep DI modules
-keep class com.phonerepair.shop.di.** { *; }

# Keep R class
-keep class com.phonerepair.shop.R { *; }
-keep class com.phonerepair.shop.R$* { *; }

# Keep BuildConfig
-keep class com.phonerepair.shop.BuildConfig { *; }

# Keep annotations
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes EnclosingMethod
-keepattributes Signature
-keepattributes InnerClasses

# Don't optimize/enumerate enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep R8 full mode compatibility
-keepattributes SourceFile,LineNumberTable

# Don't warn about missing classes
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**
-dontwarn androidx.**
-dontwarn kotlinx.**
-dontwarn coil.**
-dontwarn com.github.mikephil.charting.**
-dontwarn com.itextpdf.**
-dontwarn com.google.mlkit.**
-dontwarn androidx.camera.**
-dontwarn androidx.work.**
-dontwarn androidx.datastore.**
-dontwarn timber.log.**
-dontwarn org.threeten.**

# Optimize
-optimizationpasses 5
-allowaccessmodification
-mergeinterfacesaggressively

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}