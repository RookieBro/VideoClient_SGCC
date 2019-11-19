# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in G:\SDK/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.vidyo.**{*;}
-keep class * extends java.io.Serializable
-keep class com.telecomyt.videolibrary.ui.**{*;}
#-keep class com.telecomyt.videolibrary.utils.**{*;}
-keep class com.telecomyt.videolibrary.utils.UIUtils{*;}
-keep class com.telecomyt.videolibrary.utils.GsonUtils{*;}
-keep class com.telecomyt.videolibrary.gson.**{*;}
-keep class com.telecomyt.videolibrary.service.**{*;}
-keep class com.telecomyt.videolibrary.bean.**{*;}
-keep class com.telecomyt.videolibrary.event.**{*;}
-keep class com.telecomyt.videolibrary.callback.**{*;}
-keep class com.telecomyt.videolibrary.manager.**{*;}
-keep class com.telecomyt.videolibrary.base.**{*;}
-keep class com.telecomyt.videolibrary.VideoClient{*;}
-keep class com.telecomyt.videolibrary.VideoClientOutEvent{*;}
#-keep class com.telecomyt.videolibrary.VideoInit{*;}
-keep class com.telecomyt.videolibrary.VideoState{*;}
-keep class com.telecomyt.videolibrary.view.CircleImageView{*;}
-keep class com.telecomyt.videolibrary.Config{*;}
