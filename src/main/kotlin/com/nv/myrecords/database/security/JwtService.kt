package com.nv.myrecords.database.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.Base64
import java.util.Date

@Service
class JwtService(
    @Value("\${jwt.service}") private val jwtSecret: String
) {

    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))

    private val accessTokenValidityMs = 15L * 60L * 1000

    val refreshTokenValidityMs = 30L * 24  * 60 * 60 * 1000

    private fun generateToken(
        userId: String,
        type: String,
        expiry: Long,
    ) : String =
        Jwts.builder()
            .subject(userId)
            .claim("type", type)
            .issuedAt(Date())
            .expiration(Date(Date().time + expiry))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()

    fun generateAccessToken(userId: String) =
        generateToken(userId, "access", accessTokenValidityMs)

    fun generateRefreshToken(userId: String) =
        generateToken(userId, "refresh", refreshTokenValidityMs)

    fun validateAccessToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "access"
    }

    fun validateRefreshToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == "refresh"
    }

    fun getUserIdFromToken(token: String): String {
        val claims = parseAllClaims(token) ?: throw ResponseStatusException(
            HttpStatusCode.valueOf(404),
            "Invalid token."
        )
        return claims.subject
    }

    private fun parseAllClaims(token: String): Claims? =
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(
                    if (token.startsWith("Bearer ")) {
                        token.removePrefix("Bearer ")
                    } else token)
                .payload
        } catch (e: Exception) {
            null
        }

}