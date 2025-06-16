package com.nv.myrecords.security

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)
