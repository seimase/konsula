-keep class android.support.v4.** { *; }
-dontwarn android.support.v4.**
-keep class com.google.api.services.plus.** { *; }
-keep class com.google.api.services.auth.** { *; }
-keep class com.google.api.services.analytics.** { *; }

# Sdk
-keep public interface com.zendesk.sdk.** { *; }
-keep public class com.zendesk.sdk.** { *; }

# Appcompat and support
-keep interface android.support.v7.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v4.** { *; }
-keep class android.support.v4.** { *; }


-dontwarn com.google.code.**
-dontwarn  jp.wasabeef.recyclerview.**

#wasabeef recyclerview
-keep class jp.wasabeef.recyclerview.** { *; }
-keepattributes Signature
#HTTP Legacy
-keep class org.apache.** { *; }
-keepattributes Signature

#Support libraries
-keep class com.android.** { *; }
-keepattributes Signature


# Keep the annotations
-keepattributes *Annotation*


-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''


# Gson
-keep interface com.google.gson.** { *; }
-keep class com.google.gson.** { *; }

# Retrofit
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class retrofit.** { *; }
-keep interface retrofit.** { *; }

# Retrofit
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-dontwarn rx.**
-dontwarn retrofit.**
-dontwarn okio.**
-keep class retrofit.** { *; }
-keep class com.konsula.app.service.model.** { *; }
-keepclassmembernames interface * {
    @retrofit.http.* <methods>;
}

#Picasso

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\installer\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# We only want obfuscation
-keepattributes InnerClasses,Signature,Annotation,EnclosingMethod

# Chat SDK
-keep public interface com.zopim.android.sdk.** { *; }
-keep public class com.zopim.android.sdk.** { *; }

# OKHttp
-dontwarn com.squareup.okhttp.**

# Jackson
-keep public interface com.fasterxml.jackson.** { *; }
-keep public class com.fasterxml.jackson.** { *; }
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

-keep class com.google.common.io.Resources {
    public static <methods>;
}
-keep class com.google.common.collect.Lists {
    public static ** reverse(**);
}
-keep class com.google.common.base.Charsets {
    public static <fields>;
}

-keep class com.google.common.base.Joiner {
    public static Joiner on(String);
    public ** join(...);
}

-keep class com.google.common.collect.MapMakerInternalMap$ReferenceEntry
-keep class com.google.common.cache.LocalCache$ReferenceEntry

# http://stackoverflow.com/questions/9120338/proguard-configuration-for-guava-with-obfuscation-and-optimization
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Guava 19.0
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Veritrans
-keep class id.co.veritrans.android.api.**{ *; }


