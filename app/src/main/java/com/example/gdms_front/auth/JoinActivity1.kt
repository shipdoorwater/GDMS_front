package com.example.gdms_front.auth

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gdms_front.R
import com.example.gdms_front.databinding.ActivityJoin1Binding

class JoinActivity1 : AppCompatActivity() {

    private lateinit var binding: ActivityJoin1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_join1)
        binding = ActivityJoin1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.joinUserPw.typeface = Typeface.DEFAULT
        binding.joinUserPwConfirm.typeface = Typeface.DEFAULT

        binding.nextBtn.setOnClickListener {
            val userId = binding.joinUserId.text.toString()
            val userPw = binding.joinUserPw.text.toString()
            val userPwConfirm = binding.joinUserPwConfirm.text.toString()
            val userName = binding.joinUserName.text.toString()


            // 유효성 검사
            // 1. 이름 입력 여부
            if (userName.isEmpty()) {
                Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. 아이디 입력 여부 (영문 + 숫자, 6~12자리)
            if (userId.isEmpty()) {
                Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidUserId(userId)) {
                Toast.makeText(this, "아이디는 영문과 숫자로 6~12자리로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 3. 비밀번호 입력 여부 및 유효성 검사(영문 대소문자, 숫자, 특수문자 모두 포함한 8~20자리)
            if (userPw.isEmpty()) {
                Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidPassword(userPw)) {
                Toast.makeText(this, "비밀번호는 영문 대소문자, 숫자, 특수문자를 모두 포함한 8~20자리로 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 4. 비밀번호 일치여부
            if (userPw != userPwConfirm) {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, JoinActivity2::class.java)
            intent.putExtra("userName", userName)
            intent.putExtra("userId", userId)
            intent.putExtra("userPw", userPw)
            startActivity(intent)
        }


        binding.backButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
    }

    private fun isValidUserId(userId: String): Boolean {
        val regex = "^(?=.*[a-zA-Z])[a-zA-Z0-9]{6,12}$".toRegex()
        // 영문만으로도 6~12자리 구성 가능, 영문+숫자로도 가능, 단, 숫자로만은 불가능
        return regex.matches(userId)
    }

    private fun isValidPassword(password: String): Boolean {
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$".toRegex()
        return regex.matches(password)
    }

}