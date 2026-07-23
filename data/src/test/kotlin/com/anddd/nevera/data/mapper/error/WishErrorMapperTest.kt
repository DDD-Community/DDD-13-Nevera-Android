package com.anddd.nevera.data.mapper.error

import com.anddd.nevera.core.common.NetworkError
import com.anddd.nevera.data.testutil.httpError
import com.anddd.nevera.domain.model.common.CommonError
import com.anddd.nevera.domain.model.wish.CreateWishError
import com.anddd.nevera.domain.model.wish.UpdateWishError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** 위시 생성·수정의 서버 에러 코드 매핑을 고정한다. */
class WishErrorMapperTest {

    @Test
    fun `생성 - 서버 코드 3001은 InvalidInput이 된다`() {
        assertEquals(CreateWishError.InvalidInput, httpError(3001).toCreateWishError())
    }

    @Test
    fun `생성 - 서버 코드 2041은 MemberNotFound가 된다`() {
        assertEquals(CreateWishError.MemberNotFound, httpError(2041).toCreateWishError())
    }

    @Test
    fun `생성 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            CreateWishError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toCreateWishError(),
        )
    }

    @Test
    fun `생성 - 네트워크 연결 실패는 공통 에러로 감싼다`() {
        assertEquals(
            CreateWishError.Common(CommonError.NetworkUnavailable),
            NetworkError.NetworkConnectionError().toCreateWishError(),
        )
    }

    @Test
    fun `수정 - 서버 코드 3001은 InvalidInput이 된다`() {
        assertEquals(UpdateWishError.InvalidInput, httpError(3001).toUpdateWishError())
    }

    @Test
    fun `수정 - 서버 코드 4051은 WishNotFound가 된다`() {
        assertEquals(UpdateWishError.WishNotFound, httpError(4051).toUpdateWishError())
    }

    @Test
    fun `수정 - 서버 코드 4052는 WishForbidden이 된다`() {
        assertEquals(UpdateWishError.WishForbidden, httpError(4052).toUpdateWishError())
    }

    @Test
    fun `수정 - 서버 코드 4053은 WishAlreadyAchieved가 된다`() {
        assertEquals(UpdateWishError.WishAlreadyAchieved, httpError(4053).toUpdateWishError())
    }

    @Test
    fun `수정 - 알 수 없는 서버 코드는 공통 에러로 감싼다`() {
        assertEquals(
            UpdateWishError.Common(CommonError.ServerError("서버 메시지")),
            httpError(9999).toUpdateWishError(),
        )
    }
}
