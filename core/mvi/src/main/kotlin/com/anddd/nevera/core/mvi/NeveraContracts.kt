package com.anddd.nevera.core.mvi

/**
 * UI가 렌더링에 사용하는 불변 상태.
 * ViewModel이 보유하며, Compose는 이 상태를 구독해 화면을 그린다.
 */
interface NeveraState

/**
 * 한 번만 소비되는 단발성 이벤트.
 * 토스트 메시지, 화면 전환처럼 상태로 표현할 수 없는 부수 효과에 사용한다.
 */
interface NeveraSideEffect

/**
 * 사용자 액션 또는 외부 이벤트를 ViewModel로 전달하는 입력 신호.
 * UI는 Intent를 발행하고, ViewModel은 이를 받아 처리를 시작한다.
 */
interface NeveraIntent

/**
 * [NeveraState]를 어떻게 변경할지 기술하는 순수 변환 단위.
 * Intent 처리 결과로 생성되며, reduce 함수에 의해 새로운 State로 반영된다.
 */
interface NeveraMutation
