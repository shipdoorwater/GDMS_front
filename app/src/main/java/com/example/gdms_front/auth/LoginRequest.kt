package com.example.gdms_front.auth

import android.media.session.MediaSession.Token

data class LoginRequest (
    val userId : String,
    val userPw : String
)
//