package com.anddd.nevera.navigation

import androidx.compose.runtime.snapshots.Snapshot
import com.anddd.nevera.core.navigation.Navigator

/**
 * 딥링크가 가리키는 화면 스택을 조립한다.
 *
 * core:navigation에는 이 함수가 없다. 딥링크는 백스택 연산이 아니라 **연산 둘의 조합**이고,
 * 딥링크라는 개념을 아는 모듈은 조립 지점뿐이기 때문이다.
 *
 * 스냅샷으로 묶어 두 번의 상태 변경 사이가 화면에 보이지 않게 한다.
 */
fun Navigator.openDeeplink(target: DeeplinkTarget) {
    Snapshot.withMutableSnapshot {
        navigate(target.root.key)
        replaceStack(target.stack)
    }
}
