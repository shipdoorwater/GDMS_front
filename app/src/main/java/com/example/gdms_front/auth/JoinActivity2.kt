package com.example.gdms_front.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.gdms_front.MainActivity
import com.example.gdms_front.R
import com.example.gdms_front.alarm.FCMTokenManager
import com.example.gdms_front.databinding.ActivityJoin2Binding
import com.example.gdms_front.model.JoinRequest
import com.example.gdms_front.model.TokenUpdate
import com.example.gdms_front.network.RetrofitClient
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class JoinActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityJoin2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, JoinActivity1::class.java)
            startActivity(intent)
        }


        // 첫 번째 페이지에서 전달된 데이터를 가져옵니다.
        val userName = intent.getStringExtra("userName").toString()
        val userId = intent.getStringExtra("userId").toString()
        val userPw = intent.getStringExtra("userPw").toString()

        binding.signupSubmitButton.setOnClickListener {

            val marketingYn = binding.marketingCheckBox.isChecked
            val gpsYn = binding.gpsCheckBox.isChecked
            val pushYn = binding.pushCheckBox.isChecked
            val gender = when (binding.genderGroup.checkedRadioButtonId) {
                R.id.maleRadioButton -> 1
                R.id.femaleRadioButton -> 2
//                R.id.otherRadioButton -> 3
                else -> 0
            }
            val payType = when (binding.paymentMethodGroup.checkedRadioButtonId) {
                R.id.creditCardRadioButton -> 1
                R.id.kakaoPayRadioButton -> 2
                else -> 0
            }
            val userPhone = binding.joinPhone.text.toString()
            val userBirth = binding.joinBirth.text.toString()
            val userEmail = binding.joinEmail.text.toString()
            val userAdrs = binding.joinAdrs.text.toString()


            // 유효성 검사

            // 휴대폰 번호 유효성 검사
            if (userPhone.isEmpty()) {
                Toast.makeText(this@JoinActivity2, "휴대폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPhoneNumber(userPhone)) {
                Toast.makeText(this@JoinActivity2, "유효한 휴대폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 생년월일 유효성 검사
            if (userBirth.isEmpty()) {
                Toast.makeText(this@JoinActivity2, "생년월일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidBirthDate(userBirth)) {
                Toast.makeText(this@JoinActivity2, "생년월일을 YYYYMMDD 형식으로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 이메일 유효성 검사
            if (userEmail.isEmpty()) {
                Toast.makeText(this@JoinActivity2, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidEmail(userEmail)) {
                Toast.makeText(this@JoinActivity2, "유효한 이메일 주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 주소 유효성 검사
            if (userAdrs.isEmpty()) {
                Toast.makeText(this@JoinActivity2, "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            Log.d("JoinActivity2", "userName: $userName, userId: $userId, userPw: $userPw")
            Log.d("JoinActivity2", "marketingYn: $marketingYn, gpsYn: $gpsYn, pushYn: $pushYn")
            Log.d("JoinActivity2", "gender: $gender, payType: $payType")
            Log.d(
                "JoinActivity2",
                "userPhone: $userPhone, userBirth: $userBirth, userEmail: $userEmail, userAdrs: $userAdrs"
            )

            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                    Toast.makeText(this@JoinActivity2, "FCM 토큰을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                    return@addOnCompleteListener
                }

                val fcmToken = task.result
                Log.d("FCM", "Token: $fcmToken")

                val joinRequest = JoinRequest(
                    userId, userPw, userName, gender, userPhone, userAdrs, userEmail,
                    userBirth, pushYn, marketingYn, true, false, payType, fcmToken
                )

                //CoroutineScope(Dispatchers.IO).launch {
                lifecycleScope.launch {
                    try {
                        val response = RetrofitClient.apiService.join(joinRequest)
                        if (response.isSuccessful) {
                            // 회원가입 성공 처리
                            Toast.makeText(
                                this@JoinActivity2,
                                "success",
                                Toast.LENGTH_SHORT
                            ).show()
                            val sharedPreferences =
                                getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("token", userId)
                            editor.apply()
                            val intent =
                                Intent(this@JoinActivity2, JoinSucActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            //runOnUiThread {
                            Log.e(
                                "JoinActivity2",
                                "Join failed: ${response.errorBody()?.string()}"
                            )
                            Toast.makeText(
                                this@JoinActivity2,
                                "회원가입 실패",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@JoinActivity2,
                            "네트워크 오류 발생",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("JoinActivity2", "Join failed", e)
                    }
                }
            }
        }
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // 정규식: 010, 011, 016, 017, 018, 019로 시작하는 10-11자리 숫자
        val regex = "^01[0-1|6-9][0-9]{7,8}$".toRegex()
        return regex.matches(phone)
    }


    private fun isValidBirthDate(birth: String): Boolean {
        val regex = "^\\d{8}$".toRegex()
        if (!regex.matches(birth)) return false

        val year = birth.substring(0, 4).toInt()
        val month = birth.substring(4, 6).toInt()
        val day = birth.substring(6, 8).toInt()

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return when {
            year < 1900 || year > currentYear -> false
            month < 1 || month > 12 -> false
            day < 1 || day > 31 -> false
            else -> true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return regex.matches(email)
    }
}