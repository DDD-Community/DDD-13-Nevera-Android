# ============================================================
# Firebase Cloud Messaging
# onNewToken, onMessageReceived는 R8이 미사용으로 판단해 제거할 수 있음
# NeveraMessagingService 클래스 자체는 AndroidManifest.xml 등록으로 AGP가 자동 keep
# ============================================================
-keepclassmembers class * extends com.google.firebase.messaging.FirebaseMessagingService {
    void onNewToken(java.lang.String);
    void onMessageReceived(com.google.firebase.messaging.RemoteMessage);
}


# ============================================================
# WorkManager
# work-runtime AAR에 동일한 keep 규칙이 포함되어 있어 기술적으로는 중복
# (AAR: -keepnames + -keepclassmembers public <init>(...))
# Worker 클래스명이 WorkManager DB에 String으로 저장되고
# 프로세스 재시작 시 Class.forName()으로 복원 → 클래스명 변경 시 ClassNotFoundException
# 의도를 명시적으로 문서화하기 위해 유지
# ============================================================
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}


# ============================================================
# Hilt WorkManager (@HiltWorker)
# @AssistedInject 생성자는 Dagger가 생성한 Factory 코드에서 직접 참조
# HiltWorkerFactory가 class name → Factory 맵 조회하므로 클래스명 보존 필요
# WorkManager 규칙으로 이미 keep되나, 의도 명시를 위해 유지
# ============================================================
-keepclasseswithmembers class * {
    @dagger.assisted.AssistedInject <init>(...);
}