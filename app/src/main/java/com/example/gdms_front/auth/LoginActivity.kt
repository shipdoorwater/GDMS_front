package com.example.gdms_front.auth

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gdms_front.MainActivity
import com.example.gdms_front.databinding.ActivityLoginBinding
import com.example.gdms_front.model.LoginRequest
import com.example.gdms_front.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("LoginActivity", "onCreate")

        // SharedPreferences 가져오기
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // 저장된 사용자 정보 로드
        val savedUserId = sharedPreferences.getString("savedUserId", "")
        val savedUserPw = sharedPreferences.getString("savedUserPw", "")
        val isAutoLogin = sharedPreferences.getBoolean("isAutoLogin", false)

        binding.userPw.typeface = Typeface.DEFAULT

        binding.userId.setText(savedUserId)
        binding.userPw.setText(savedUserPw)
        if (savedUserId != null) {
            if (savedUserPw != null) {
                binding.saveCredentialsCheckBox.isChecked = savedUserId.isNotEmpty() && savedUserPw.isNotEmpty()
            }
        }
        binding.autoLoginCheckBox.isChecked = isAutoLogin

        if (savedUserId != null) {
            if (savedUserPw != null) {
                if (isAutoLogin && savedUserId.isNotEmpty() && savedUserPw.isNotEmpty()) {
                    // 자동 로그인
                    login(savedUserId, savedUserPw)
                }
            }
        }

        binding.loginBtn.setOnClickListener {
            val userId = binding.userId.text.toString()
            val userPw = binding.userPw.text.toString()

            Log.d("LoginActivity", "userId: $userId, userPw: $userPw")

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.login(LoginRequest(userId, userPw))
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        Log.d("LoginActivity", "Login successful: ${response.body()}")
                        Log.d("LoginActivity", "token: ${loginResponse?.message}")
                        Log.d("LoginActivity", "userId: $userId")

                        // 로그인 성공 처리
                        val editor = sharedPreferences.edit()
                        editor.putString("token", userId)

                        if (binding.saveCredentialsCheckBox.isChecked) {
                            editor.putString("savedUserId", userId)
                            editor.putString("savedUserPw", userPw)
                        } else {
                            editor.remove("savedUserId")
                            editor.remove("savedUserPw")
                        }

                        editor.putBoolean("isAutoLogin", binding.autoLoginCheckBox.isChecked)
                        editor.apply()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        // 로그인 실패 처리
                        Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                        Log.d("LoginActivity", "Login failed: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    // 네트워크 오류 처리
                    Toast.makeText(this@LoginActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Login failed", e)
                }
            }
        }

        binding.joinBtn.setOnClickListener {
            val intent = Intent(this, JoinActivity1::class.java)
            startActivity(intent)
        }
    }

    private fun login(userId: String?, userPw: String?) {
        // 로그인 로직을 여기서 호출 (위의 loginBtn 클릭 시와 동일하게 구현)
        if (userId != null && userPw != null) {
            binding.userId.setText(userId)
            binding.userPw.setText(userPw)
            binding.loginBtn.performClick()
        }
    }
}

