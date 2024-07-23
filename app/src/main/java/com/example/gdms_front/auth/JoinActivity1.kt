package com.example.gdms_front.auth

import android.content.Intent
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




        binding.nextBtn.setOnClickListener {
            val userId = binding.joinUserId.text.toString()
            val userPw = binding.joinUserPw.text.toString()
            val userPwConfirm = binding.joinUserPwConfirm.text.toString()
            val userName = binding.joinUserName.text.toString()

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

}