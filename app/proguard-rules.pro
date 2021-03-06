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

-dontwarn java.awt.**
-dontwarn javax.annotation.**
-dontwarn javax.imageio.**
-dontwarn com.facebook.infer.**
-dontwarn org.bouncycastle.**
-dontwarn com.itextpdf.**
-dontwarn javax.xml.crypto.**
-dontwarn org.apache.**
-dontwarn java.nio.**
-dontwarn java.lang.**
-dontwarn org.codehaus.**