# ============================================================
# Stack Trace (크래시 리포트 대비)
# ============================================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile


# ============================================================
# Kotlin
# ============================================================
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.**


# ============================================================
# Domain Models (kotlin.jvm 모듈 - consumerProguardFiles 불가)
# Retrofit 응답을 매핑한 결과물이므로 필드명 보존 불필요하나,
# 리플렉션/Navigation 인자 전달 등 대비해 클래스 유지
# ============================================================
-keep class com.anddd.nevera.domain.model.** { *; }


# ============================================================
# Core Common (kotlin.jvm 모듈 - consumerProguardFiles 불가)
# sealed interface 계층은 when 분기 최적화 과정에서 제거될 수 있음
# ============================================================
-keep class com.anddd.nevera.core.common.** { *; }


# ============================================================
# Hilt / Dagger (AAR 내 번들 규칙 보완)
# ============================================================
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}


# ============================================================
# Jetpack Navigation (타입 안전 Navigation 인자)
# ============================================================
-keepnames class * implements android.os.Parcelable
-keepnames class * implements java.io.Serializable
