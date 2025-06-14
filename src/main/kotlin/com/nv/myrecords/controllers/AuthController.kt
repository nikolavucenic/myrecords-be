package com.nv.myrecords.controllers

import com.nv.myrecords.database.security.AuthService
import com.nv.myrecords.database.security.TokenPair
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/auth"])
class AuthController(
    private val authService: AuthService
) {

    data class AuthRequest(
        @field:Email(message = "Invalid email format.")
        val email: String,
        @field:Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$",
            message = "Password must be at least 9 characters long and contain at least one digit, uppercase and lowercase character."
        )
        val password: String,
    )

    data class RefreshRequest(
        val refreshToken: String
    )

    @PostMapping(path = ["/register"])
    fun register(
        @Valid @RequestBody body: AuthRequest
    ) {
        authService.register(body.email, body.password)
    }

    @PostMapping(path = ["/login"])
    fun login(
        @RequestBody body: AuthRequest
    ): TokenPair =
        authService.login(body.email, body.password)

    @PostMapping(path = ["/refresh"])
    fun refresh(
        @RequestBody body: RefreshRequest
    ): TokenPair =
        authService.refresh(body.refreshToken)

}