package com.anddd.nevera.data.mapper

import com.anddd.nevera.data.model.wish.WishResponse
import com.anddd.nevera.domain.model.wish.Wish

internal fun WishResponse.toDomain(): Wish = Wish(
    id = id,
    name = name,
    amount = amount,
)
