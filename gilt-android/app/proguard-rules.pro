# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Classes that will be serialized/deserialized over Gson
-keep class com.optimizely.JSON.** { *; }

# OkIO and OkHTTP
-dontwarn okio.**

-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn com.optimizely.integrations.**
-dontwarn com.mixpanel.android.mpmetrics.MixpanelApiRetriever*
-dontwarn com.optimizely.Preview.OptimizelySwitchVariationInfoView