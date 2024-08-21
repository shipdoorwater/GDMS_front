package com.example.gdms_front.auth

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.ServerError
import com.example.gdms_front.MainActivity
import com.example.gdms_front.R
import com.example.gdms_front.databinding.ActivityLoginBinding
import com.example.gdms_front.model.KakaoLoginRequest
import com.example.gdms_front.model.KakaoLoginResponse
import com.example.gdms_front.model.LoginRequest
import com.example.gdms_front.network.RetrofitClient
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthError
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val kakaoAppKey = getString(R.string.kakao_app_key)
        KakaoSdk.init(this, kakaoAppKey)
        setupViews()
        checkAutoLogin()

        val joinBtn = findViewById<TextView>(R.id.joinBtn)
        joinBtn.setOnClickListener {
            val intent = Intent(this, JoinActivity1::class.java)
            startActivity(intent)
        }
    }


    private fun setupViews() {
        binding.loginBtn.setOnClickListener { performGeneralLogin() }
        binding.kakaoLoginBtn.setOnClickListener { performKakaoLogin() }
        binding.userPw.typeface = Typeface.DEFAULT

        // 저장된 일반 로그인 정보 로드
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val savedUserId = sharedPreferences.getString("savedUserId", "")
        val savedUserPw = sharedPreferences.getString("savedUserPw", "")
        val isAutoLogin = sharedPreferences.getBoolean("isAutoLogin", false)

        binding.userId.setText(savedUserId)
        binding.userPw.setText(savedUserPw)
        binding.autoLoginCheckBox.isChecked = isAutoLogin
    }

    private fun checkAutoLogin() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isAutoLogin = sharedPreferences.getBoolean("isAutoLogin", false)
        val savedUserId = sharedPreferences.getString("savedUserId", "")
        val savedUserPw = sharedPreferences.getString("savedUserPw", "")

        if (isAutoLogin && !savedUserId.isNullOrEmpty() && !savedUserPw.isNullOrEmpty()) {
            performGeneralLogin(savedUserId, savedUserPw)
        }
    }

    private fun performGeneralLogin(userId: String? = null, userPw: String? = null) {
        val id = userId ?: binding.userId.text.toString()
        val pw = userPw ?: binding.userPw.text.toString()

        if (id.isEmpty() || pw.isEmpty()) {
            Toast.makeText(this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // FCM 토큰 가져오기
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "FCM 토큰 가져오기 실패", task.exception)
                return@addOnCompleteListener
            }

            val fcmToken = task.result
            loginWithCredentials(id, pw, fcmToken)
        }
    }

    private fun loginWithCredentials(userId: String, userPw: String, fcmToken: String?) {
        lifecycleScope.launch {
            try {
                val response =
                    RetrofitClient.apiService.login(LoginRequest(userId, userPw, fcmToken))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        handleSuccessfulLogin(userId, userPw, loginResponse.message)
                    } else {
                        handleFailedLogin("로그인 응답이 비어있습니다.")
                    }
                } else {
                    handleFailedLogin("로그인 실패")
                }
            } catch (e: Exception) {
                handleFailedLogin("네트워크 오류 발생")
            }
        }
    }

    private fun handleSuccessfulLogin(userId: String, userPw: String, message: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", userId)

        if (binding.autoLoginCheckBox.isChecked) {
            editor.putString("savedUserId", userId)
            editor.putString("savedUserPw", userPw)
            editor.putBoolean("isAutoLogin", true)
        } else {
            editor.remove("savedUserId")
            editor.remove("savedUserPw")
            editor.putBoolean("isAutoLogin", false)
        }

        editor.putBoolean("isKakaoLoggedIn", false)
        editor.apply()

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        navigateToMainActivity()
    }

    private fun performKakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this, callback = kakaoLoginCallback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = kakaoLoginCallback)
        }
    }

    private val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("KakaoLogin", "로그인 실패", error)
            Log.e("KakaoLogin", "에러 메시지: ${error.message}")

            when (error) {
                is ClientError -> Log.e("KakaoLogin", "클라이언트 에러, 에러코드: ${error.reason}")
                is AuthError -> Log.e("KakaoLogin", "인증 에러, 에러코드: ${error.reason}")
                is ServerError -> Log.e("KakaoLogin", "서버 에러, 에러코드: ${error.message}")
                else -> Log.e("KakaoLogin", "기타 에러")
            }

            handleFailedLogin("카카오 로그인 실패: ${error.message}")
        } else if (token != null) {
            Log.i("KakaoLogin", "로그인 성공: ${token.accessToken}")
            sendKakaoTokenToServer(token.accessToken)
        }
    }

    private fun sendKakaoTokenToServer(accessToken: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            val fcmToken = if (task.isSuccessful) task.result else null
            val request = KakaoLoginRequest(accessToken, fcmToken)

            RetrofitClient.apiService.kakaoLogin(request)
                .enqueue(object : Callback<KakaoLoginResponse> {
                    override fun onResponse(
                        call: Call<KakaoLoginResponse>,
                        response: Response<KakaoLoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            if (loginResponse != null) {
                                handleSuccessfulKakaoLogin(
                                    loginResponse.userId,
                                    loginResponse.message
                                )
                            } else {
                                handleFailedLogin("카카오 로그인 응답 처리 실패")
                            }
                        } else {
                            handleFailedLogin("카카오 서버 로그인 실패")
                        }
                    }

                    override fun onFailure(call: Call<KakaoLoginResponse>, t: Throwable) {
                        handleFailedLogin("카카오 서버 통신 실패")
                    }
                })
        }
    }

    private fun handleSuccessfulKakaoLogin(userId: String?, message: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", userId)
        editor.putBoolean("isKakaoLoggedIn", true)
        editor.remove("savedUserId")
        editor.remove("savedUserPw")
        editor.putBoolean("isAutoLogin", false) // 카카오 로그인은 자동 로그인 사용 안 함
        editor.apply()

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        navigateToMainActivity()
    }

    private fun handleFailedLogin(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.e("LoginActivity", message)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}