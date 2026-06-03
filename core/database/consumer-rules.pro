# ============================================================
# Room (공식 규칙: room-runtime AAR 번들 기준)
# 번들 규칙: -keep class * extends RoomDatabase { void <init>(); }
#            -dontwarn androidx.room.paging.**
# ============================================================

# Room 번들 규칙보다 강하게 전체 keep (Database 구현체 리플렉션 대비)
-keep class * extends androidx.room.RoomDatabase { *; }

# Room 번들에 없는 보완 규칙 — @Entity / @Dao는 KSP 생성 코드에서 직접 참조하나
# 난독화 시 클래스·멤버명 변경을 방지하기 위해 명시적으로 유지
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface *

-dontwarn androidx.room.paging.**
