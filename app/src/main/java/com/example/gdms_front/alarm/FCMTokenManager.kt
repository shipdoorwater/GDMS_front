package com.example.gdms_front.alarm

import android.content.Context
import android.util.Log
import com.example.gdms_front.model.TokenUpdate
import com.example.gdms_front.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FCMTokenManager {
    private const val TAG = "FCMTokenManager"

    fun handleFCMToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("FCM_PREF", Context.MODE_PRIVATE)
        val savedToken = sharedPreferences.getString("FCM_TOKEN", null)

        if (savedToken == null || savedToken != token) {
            sendTokenToServer(context, token)
            sharedPreferences.edit().putString("FCM_TOKEN", token).apply()
        }
    }

    private fun sendTokenToServer(context: Context, token: String) {
        val sharedPreference = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference.getString("token", null)

        if (userId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.tokenApiService.updateToken(TokenUpdate(userId, token))
                    if (response.isSuccessful) {
                        Log.d(TAG, "FCMToken 전송 성공")
                    } else {
                        Log.d(TAG, "FCMToken 전송 실패. 상태 코드: ${response.code()}")
                        Log.d(TAG, "에러 메시지: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "네트워크 오류", e)
                }
            }
        } else {
            Log.d(TAG, "UserId가 null")
        }
    }
}