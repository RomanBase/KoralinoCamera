# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/romanhornak/Library/Android/sdk/tools/proguard/proguard-android.txt
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

-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes *Annotation*

### For android appcompat support library - https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-support-v7-appcompat.pro
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-keepclassmembers public class * {
    @android.support.annotation.Keep *;
}

-keep android.support.annotation.Keep public class { *; }

### For android cardview - https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-support-v7-cardview.pro
-keep class android.support.v7.widget.RoundRectDrawable { *; }

### Glide https://github.com/bumptech/glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

### GSON 2.2.4 specific rules - https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-gson.pro
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
