package com.example.gdms_front.myPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.gdms_front.R
import com.example.gdms_front.model.MemberInfoResponse
import com.example.gdms_front.network.MyPageApiService
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {

    private lateinit var myPageApiService: MyPageApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        myPageApiService = RetrofitClient.myPageApiService

        val sharedPreference = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)
        Log.d("MyPageTest", "user id: ${userId}")

        if (userId != null) {
            getMemberInfo(userId)
        } else {
            Log.d("MyPageActivity", "ID를 불러올 수 없습니다")
        }

        // 뒤로 가기
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            finish()
        }

        // 수정화면으로 이동
        findViewById<TextView>(R.id.modifyBtn).setOnClickListener {
            val intent = Intent(this, ModifyInfoActivity::class.java)
            startActivity(intent)
        }

    }


    private fun getMemberInfo(userId: String) {
        myPageApiService.getMemberInfo(userId).enqueue(object : Callback<MemberInfoResponse> {
            override fun onResponse(
                call: Call<MemberInfoResponse>,
                response: Response<MemberInfoResponse>
            ) {
                if (response.isSuccessful) {
                    val memberInfo = response.body()
                    if (memberInfo != null) {

                        findViewById<TextView>(R.id.userName_Header).text = memberInfo.userName
                        findViewById<TextView>(R.id.userName_myPage_main).text = memberInfo.userName
                        findViewById<TextView>(R.id.userBirthday_myPage_main).text = memberInfo.birthDate
                        findViewById<TextView>(R.id.userPhone_myPage_main).text = memberInfo.userPhone
                        findViewById<TextView>(R.id.userEmail_myPage_main).text = memberInfo.userEmail
                        findViewById<TextView>(R.id.userAddress_myPage_main).text = memberInfo.userAddress
                        loadProfileImage(memberInfo.profileUrl)
                        Log.d("MyPageAPITest", "profileUrl response 확인 :  ${memberInfo.profileUrl}")

                    } else {
                        Toast.makeText(
                            this@MyPageActivity,
                            "회원 정보를 가져올 수 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 404) {
                    Toast.makeText(this@MyPageActivity, "사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this@MyPageActivity,
                        "회원 정보를 가져오는 중 오류가 발생했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<MemberInfoResponse>, t: Throwable) {
                Toast.makeText(
                    this@MyPageActivity,
                    "네트워크 오류가 발생했습니다: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun loadProfileImage(profileUrl: String?) {
        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        if (profileUrl != null) {
            val fullUrl = "http://192.168.0.73:8080$profileUrl" // 서버 URL을 추가하세요.
            Log.d("MyPageAPITest", "fullURL 주소 : ${fullUrl}")
            Glide.with(this)
                .load(fullUrl)
                .placeholder(R.drawable.person_add_circle)
                .error(R.drawable.person_add_circle)
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.person_add_circle)
        }
    }
}