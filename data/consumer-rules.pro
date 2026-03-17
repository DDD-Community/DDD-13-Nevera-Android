# ============================================================
# Retrofit Response Models
# @SerializedName 미사용 → Gson이 필드명으로 JSON 키 매핑
# 난독화 시 필드명이 변경되면 역직렬화 실패하므로 보존
# ============================================================
-keepclassmembers class com.anddd.nevera.data.model.** {
    <fields>;
}


# ============================================================
# DataStore
# Proto DataStore 미사용 (Preferences DataStore) → 별도 규칙 불필요
# 향후 Proto DataStore 도입 시 아래 주석 해제
# ============================================================
# -keep class com.anddd.nevera.data.datastore.** { *; }
