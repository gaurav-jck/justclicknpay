     # Add project specific ProGuard rules here.
# By  default, the flags in this file are appended to flags specified
# in C :\Users\Lenovo\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You c an edit the include path and order by changing the proguardFiles
# direct ive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class Name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file Name.
#-renamesourcefileattribute SourceFile

#-dontwarn java.awt.**
#-dontwarn javax.annotation.**
#-dontwarn javax.imageio.**
#-dontwarn com.facebook.infer.**
#-dontwarn org.bouncycastle.**
#-dontwarn com.itextpdf.**
#-dontwarn javax.xml.crypto.**
#-dontwarn org.apache.**
#-dontwarn java.nio.**
#-dontwarn java.lang.**
#-dontwarn org.codehaus.**

#-keep class com.justclick.clicknbook.model.LoginModel
-keep class com.justclick.clicknbook.model.LoginModel$* { *; }
-keepclassmembers class com.justclick.clicknbook.model.LoginModel { *; }

-keep class com.justclick.clicknbook.Fragment.train.model.TrainPreBookResponse$* { *; }
-keepclassmembers class com.justclick.clicknbook.Fragment.train.model.TrainPreBookResponse { *; }

-keep class com.service.finopayment.** { *; }
-dontwarn com.service.finopayment.**

# Optimizely
-keep class com.optimizely.ab.config.**
-keepclassmembers class  com.optimizely.ab.config.** {
    *;
}

# Keep Payload classes that get sent to Optimizely's backend
-keep class com.optimizely.ab.event.internal.payload.** { *; }

# Safely ignore warnings about other libraries since we are using Gson
-dontwarn com.fasterxml.jackson.**
-dontwarn org.json.**

# Annotations
-dontwarn javax.annotation.**

# slf4j
-dontwarn org.slf4j.**
-keep class org.slf4j.** {*;}

# Android Logger
-keep class com.noveogroup.android.log.** { *; }

-optimizations !class/unboxing/enum

-dontwarn com.google.gson.**
-dontwarn com.optimizely.ab.config.parser.**

# Gson (https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg)
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
##---------------End: proguard configuration for Gson  ----------


-keep class org.greenrobot.greendao.**
-keepclassmembers class org.greenrobot.greendao.** { *; }
-keep class de.greenrobot.**
-keepclassmembers class de.greenrobot.** { *; }
-dontwarn org.**
-keep class org.** {*;}
-keepclassmembers class org.** {*;}

-keep class my.dao.package.*$Properties {
    public static <fields>;
}

-keepclassmembers class my.dao.package.** {
    public static final <fields>;
    }