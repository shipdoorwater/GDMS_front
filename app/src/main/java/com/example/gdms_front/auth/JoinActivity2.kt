package com.example.gdms_front.auth

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
import com.example.gdms_front.databinding.ActivityJoin2Binding
import com.example.gdms_front.model.JoinRequest
import com.example.gdms_front.network.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JoinActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityJoin2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener{
            val intent = Intent(this, JoinActivity1::class.java)
            startActivity(intent) }



            // 첫 번째 페이지에서 전달된 데이터를 가져옵니다.
        val userName = intent.getStringExtra("userName").toString()
        val userId = intent.getStringExtra("userId").toString()
        val userPw = intent.getStringExtra("userPw").toString()

        binding.signupSubmitButton.setOnClickListener {

            val marketingYn = binding.marketingCheckBox.isChecked
            val gpsYn = binding.gpsCheckBox.isChecked
            val pushYn = binding.pushCheckBox.isChecked
            val gender = when(binding.genderGroup.checkedRadioButtonId) {
                R.id.maleRadioButton -> 1
                R.id.femaleRadioButton -> 2
                R.id.otherRadioButton -> 3
                else -> 0
            }
            val payType = when(binding.paymentMethodGroup.checkedRadioButtonId) {
                R.id.creditCardRadioButton -> 1
                R.id.kakaoPayRadioButton -> 2
                else -> 0
            }
            val userPhone = binding.joinPhone.text.toString()
            val userBirth = binding.joinBirth.text.toString()
            val userEmail = binding.joinEmail.text.toString()
            val userAdrs = binding.joinAdrs.text.toString()

            Log.d("JoinActivity2", "userName: $userName, userId: $userId, userPw: $userPw")
            Log.d("JoinActivity2", "marketingYn: $marketingYn, gpsYn: $gpsYn, pushYn: $pushYn")
            Log.d("JoinActivity2", "gender: $gender, payType: $payType")
            Log.d("JoinActivity2", "userPhone: $userPhone, userBirth: $userBirth, userEmail: $userEmail, userAdrs: $userAdrs")


            val joinRequest = JoinRequest(userId, userPw, userName, gender, userPhone, userAdrs, userEmail,
                userBirth, pushYn, marketingYn, true, false, payType) //gpsYn 일단 없음

            //CoroutineScope(Dispatchers.IO).launch {
            lifecycleScope.launch {
                try{
                    val response = RetrofitClient.apiService.join(joinRequest)
                    if (response.isSuccessful) {
                        // 회원가입 성공 처리
                        Toast.makeText(this@JoinActivity2, "success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@JoinActivity2, JoinSucActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        //runOnUiThread {
                            Toast.makeText(this@JoinActivity2, "failed", Toast.LENGTH_SHORT).show()
                            Log.e("JoinActivity2", "Join failed: ${response.errorBody()?.string()}")
                        //}
                    }
                } catch (e:Exception) {
                  //  runOnUiThread {
                        Toast.makeText(this@JoinActivity2, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                        Log.e("JoinActivity2", "Join failed", e)
                  //  }
                }
            }
        }
    }
}