package com.nv.myrecords.database.security

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)
