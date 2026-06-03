# ============================================================
# Retrofit (공식 규칙: retrofit-2.11.0 META-INF/proguard/retrofit2.pro 기준)
# ============================================================
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Retrofit 메서드 파라미터·어노테이션 보존
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# R8 full mode: Retrofit 인터페이스 자체가 제거되는 것을 방지
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# R8 full mode: 인터페이스 상속 계층 보존
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# R8 full mode: suspend 함수가 Continuation 제네릭 타입 정보를 잃지 않도록 보존
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode: Retrofit 메서드 반환 타입 클래스의 제네릭 시그니처 보존
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

-keep,allowobfuscation,allowshrinking class retrofit2.Response

-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*


# ============================================================
# OkHttp (공식 규칙: okhttp-4.12.0 META-INF/proguard/okhttp3.pro 기준)
# ============================================================
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn org.codehaus.mojo.animal_sniffer.*


# ============================================================
# ApiResponse / ApiError
# Gson TypeAdapter가 result 필드에 T의 제네릭 정보를 올바르게 바인딩하려면
# 클래스 전체(-keep { *; })가 필요하다.
# <fields>만 유지하면 R8 full mode에서 필드·생성자가 제거되어
# result 필드를 Object로 폴백, LinkedTreeMap이 반환된다.
# ============================================================
-keep class com.anddd.nevera.core.network.model.** { *; }


# ============================================================
# Gson (retrofit-converter-gson 사용)
# @SerializedName 없이 필드명으로 JSON 매핑하므로
# TypeAdapter 관련 클래스 보존
# ============================================================
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
