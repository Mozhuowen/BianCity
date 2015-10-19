# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/mozhuowen/Library/Android/sdk/tools/proguard/proguard-android.txt
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
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes Signature
-keepattributes Exceptions,InnerClasses,Signature


#-libraryjars D:\Android\WorkSpace\WeiboSDK\bin\weibosdk.jar
#-libraryjars /Users/mozhuowen/Documents/workspace/WeiboSDK/bin/weibosdk.jar
#-libraryjars ./libs/httpcore-4.4.3.jar
#-libraryjars ./libs/aes-jre1.6.jar
#-libraryjars ./libs/AMap_3DMap_V2.4.0.jar
#-libraryjars ./libs/Android_2DMapApi_V2.4.0.jar
##-libraryjars ./libs/Android_Location_V1.3.1.jar
#-libraryjars ./libs/android-async-http-1.4.6.jar
#-libraryjars ./libs/android-support-v4.jar
#-libraryjars ./libs/gson-2.2.4.jar
#-libraryjars ./libs/commons-codec-1.9.jar
#-libraryjars ./libs/mta-sdk-1.6.2.jar
#-libraryjars ./libs/open_sdk_r4547.jar
##-libraryjars ./libs/volley.jar
#-libraryjars ./libs/armeabi/libamapv304.so
#-libraryjars ./libs/armeabi/libamapv304ex.so
#-libraryjars ./libs/armeabi/libweibosdkcore.so
#-libraryjars ./libs/armeabi-v7a/libamapv304.so
#-libraryjars ./libs/armeabi-v7a/libamapv304ex.so
#-libraryjars ./libs/armeabi-v7a/libweibosdkcore.so
#-libraryjars D:\Android\adt-bundle-windows-x86_64-20140702\sdk\platforms\android-19\android.jar

-keep class com.baidu.** { *; }
-keep class com.amap.** { *; }
-keep class com.qq.** { *; }
-keep class android.support.v4.** { *; }
-keep class org.apache.** { *; }
-keep class com.tencent.** { *; }
-keep class com.sina.** { *; }
-keep class com.putaotown.net.objects.** { *; }
-keep class com.putaotown.community.models.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.putaotown.geographic.** { *; }

#高德相关混淆文件
#3D 地图
-keep   class com.amap.api.maps2d.**{*;}
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
#Location
-keep   class com.amap.api.location.**{*;}

-dontwarn android.support.v4.**
-dontwarn android.support.v7.**
#-dontwarn org.apache.commons.net.**
-dontwarn com.tencent.**
-dontwarn com.qq.**
-dontwarn com.amap.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.**
-dontwarn com.xiaomi.**
#-dontwarn com.sina.**


-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class * extends com.google.gson.**

-keep class com.uuhelper.Application.** { *; }
-keep class net.sourceforge.zbar.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class android.support.v4.** { *; }
-keep class android.app.** { *; }

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}