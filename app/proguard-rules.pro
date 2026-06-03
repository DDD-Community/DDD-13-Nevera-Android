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
# 직접 코드 참조로만 사용 → R8이 사용된 멤버를 자동 추적해 보존
# 클래스 제거·리네임만 방지하면 충분하므로 { *; } 미사용
# ============================================================
-keep class com.anddd.nevera.domain.model.**


# ============================================================
# Core Common (kotlin.jvm 모듈 - consumerProguardFiles 불가)
# NeveraResult·NetworkError sealed 계층: 직접 코드 참조로 R8이 추적
# 클래스 제거·리네임만 방지
# ============================================================
-keep class com.anddd.nevera.core.common.**


# ============================================================
# Firebase ComponentRegistrar
# ComponentDiscovery가 리플렉션으로 no-arg constructor를 호출
# R8이 구현체를 제거하면 FirebaseApp 초기화 시 NoSuchMethodException 발생
# ============================================================
-keep class * implements com.google.firebase.components.ComponentRegistrar { <init>(); }


# ============================================================
# Hilt / Dagger (AAR 내 번들 규칙 보완)
# ============================================================
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel { *; }
-keepclasseswithmembers class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}
