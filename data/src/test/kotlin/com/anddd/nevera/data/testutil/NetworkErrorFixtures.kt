package com.anddd.nevera.data.testutil

import com.anddd.nevera.core.common.NetworkError

/**
 * 에러 매퍼 테스트에서 쓰는 [NetworkError] 픽스처.
 *
 * 서버가 내려준 HTTP 에러를 흉내 낸다. 매퍼 테스트는 코드→도메인 에러 매핑만 검증하므로
 * 메시지는 기본값으로 채우고, 서버 메시지가 그대로 전달되는지 확인해야 하는 케이스에서만 넘긴다.
 */
internal fun httpError(code: Int, message: String? = "서버 메시지"): NetworkError =
    NetworkError.HttpError(code = code, message = message)
