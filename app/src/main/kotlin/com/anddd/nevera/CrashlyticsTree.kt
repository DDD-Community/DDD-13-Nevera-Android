package com.anddd.nevera

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // DEBUG/INFO/VERBOSE는 전송하지 않음 — Crashlytics 세션 로그 한도(64KB) 절약
        if (priority >= Log.WARN) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            // breadcrumb: 독립 이슈를 생성하지 않고, 다음 비정상 종료 리포트에 맥락 정보로 첨부됨
            val prefix = tag?.let { "$it: " }.orEmpty()
            crashlytics.log(prefix + message)

            if (t != null) {
                // non-fatal 이슈로 기록 — 앱이 종료되지 않아도 Crashlytics 대시보드에 독립 이슈 생성
                crashlytics.recordException(t)
            }
        }
    }
}
