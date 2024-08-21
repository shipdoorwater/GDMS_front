package com.example.gdms_front.auth

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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

        // 저장된 상태가 있다면 복원
        savedInstanceState?.let { restoreState(it) }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.btnSearchAddress.setOnClickListener {
            val intent = Intent(this@JoinActivity2, SearchLocationActivity::class.java)
            getSearchResult.launch(intent)
        }

        // 주소 입력 필드를 읽기 전용으로 설정
        binding.joinAdrs.isFocusable = false
        binding.joinAdrs.isClickable = true
        binding.joinAdrs.isCursorVisible = false

        // 첫 번째 페이지에서 전달된 데이터를 가져옵니다.
        val userName = intent.getStringExtra("userName").toString()
        val userId = intent.getStringExtra("userId").toString()
        val userPw = intent.getStringExtra("userPw").toString()

        binding.signupSubmitButton.setOnClickListener {

            val marketingYn = binding.marketingCheckBox.isChecked
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

            val mainAddress = binding.joinAdrs.text.toString()
            val detailAddress = binding.etAddressDetail.text.toString()
            val userAdrs = combineAddress(mainAddress, detailAddress)


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
            if (mainAddress.isEmpty()) {
                Toast.makeText(this@JoinActivity2, "주소를 검색해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (detailAddress.isEmpty()) {
                Toast.makeText(this@JoinActivity2, "상세주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
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
                                "회원가입이 성공적으로 처리되었습니다",
                                Toast.LENGTH_SHORT
                            ).show()

                            // 입력 필드 초기화
                            clearAllInputs()

                            // 저장된 인스턴스 상태 초기화
                            clearSavedState()

                            val sharedPreferences =
                                getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("token", userId)
                            editor.apply()
                            val intent = Intent(this@JoinActivity2, JoinSucActivity::class.java)
                            intent.putExtra("pushYn",pushYn.toString())
                            Log.d("pushYn", pushYn.toString())
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

    private val getSearchResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val roadAddress = data.getStringExtra("EXTRA_ROAD_ADDR")
                    val jibunAddress = data.getStringExtra("EXTRA_JIBUN_ADDR")

                    // 주소 정보를 사용하여 UI 업데이트
                    binding.joinAdrs.setText(roadAddress)

                    // 필요하다면 jibunAddress도 사용할 수 있습니다
                    Log.d("Address", "Road: $roadAddress, Jibun: $jibunAddress")
                }
            }
        }

    // 상태 저장
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState) {
            putString("userPhone", binding.joinPhone.text.toString())
            putString("userBirth", binding.joinBirth.text.toString())
            putString("userEmail", binding.joinEmail.text.toString())
            putString("userAdrs", binding.joinAdrs.text.toString())
            putString("userAdrsDetail", binding.etAddressDetail.text.toString())
            putBoolean("marketingYn", binding.marketingCheckBox.isChecked)
            putBoolean("pushYn", binding.pushCheckBox.isChecked)
            putInt("gender", binding.genderGroup.checkedRadioButtonId)
            putInt("payType", binding.paymentMethodGroup.checkedRadioButtonId)
        }
    }

    // 상태 복원
    private fun restoreState(savedInstanceState: Bundle) {
        with(savedInstanceState) {
            binding.joinPhone.setText(getString("userPhone"))
            binding.joinBirth.setText(getString("userBirth"))
            binding.joinEmail.setText(getString("userEmail"))
            binding.joinAdrs.setText(getString("userAdrs"))
            binding.etAddressDetail.setText(getString("userAdrsDetail", ""))
            binding.marketingCheckBox.isChecked = getBoolean("marketingYn")
            binding.pushCheckBox.isChecked = getBoolean("pushYn")
            binding.genderGroup.check(getInt("gender"))
            binding.paymentMethodGroup.check(getInt("payType"))
        }
    }


    // 주소와 상세 주소를 합치는 함수
    private fun combineAddress(mainAddress: String, detailAddress: String): String {
        return if (detailAddress.isNotEmpty()) {
            "$mainAddress, $detailAddress"
        } else {
            mainAddress
        }
    }

    // 모든 입력 정보를 지우는 함수
    private fun clearAllInputs() {
        binding.joinPhone.text.clear()
        binding.joinBirth.text.clear()
        binding.joinEmail.text.clear()
        binding.joinAdrs.text.clear()
        binding.etAddressDetail.text.clear()
        binding.marketingCheckBox.isChecked = false
        binding.pushCheckBox.isChecked = false
        binding.genderGroup.clearCheck()
        binding.paymentMethodGroup.clearCheck()
    }

    // 저장된 인스턴스 상태를 초기화하는 함수
    private fun clearSavedState() {
        val emptyBundle = Bundle()
        onSaveInstanceState(emptyBundle)
        // 이렇게 하면 다음에 onCreate가 호출될 때 빈 번들이 전달됩니다.
    }
}