# ============================================================
# Retrofit Response / Request Models
# Gson은 getDeclaredFields()로 필드를 찾아 반사적으로 값을 설정한다.
# -keepclassmembers { <fields> }만으로는 R8 full mode에서 필드가 제거되며
# TypeAdapter가 생성되지 않아 LinkedTreeMap으로 폴백한다.
# 클래스 전체(-keep { *; })를 보존해야 필드/생성자/메서드가 모두 유지된다.
# ============================================================
-keep class com.anddd.nevera.data.model.** { *; }
