package com.example.gdms_front.myPage

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.gdms_front.R
import com.example.gdms_front.model.MemberInfoResponse
import com.example.gdms_front.network.MyPageApiService
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyInfoActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var modifyButton: CardView
    private lateinit var backButton: ImageView
    private lateinit var myPageApiService: MyPageApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_info)

        // Initialize views
        nameEditText = findViewById(R.id.nameSpace)
        phoneEditText = findViewById(R.id.phoneSpace)
        addressEditText = findViewById(R.id.addressSpace)
        modifyButton = findViewById(R.id.changeBtn)
        backButton = findViewById(R.id.backBtn)

        // Initialize API service
        myPageApiService = RetrofitClient.myPageApiService

        // Load current user info
        loadUserInfo()

        // Set up button click listeners
        modifyButton.setOnClickListener {
            updateUserInfo()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadUserInfo() {
        val sharedPreference = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreference.getString("token", null)

        userId?.let {
            myPageApiService.getMemberInfo(it).enqueue(object : Callback<MemberInfoResponse> {
                override fun onResponse(call: Call<MemberInfoResponse>, response: Response<MemberInfoResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { userInfo ->
                            nameEditText.setText(userInfo.userName)
                            phoneEditText.setText(userInfo.userPhone)
                            addressEditText.setText(userInfo.userAddress)
                        }
                    } else {

                    }
                }

                override fun onFailure(call: Call<MemberInfoResponse>, t: Throwable) {

                }
            })
        }
    }

    private fun updateUserInfo() {
        val sharedPreference = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreference.getString("token", null)

        val updatedInfo = mapOf(
            "userName" to nameEditText.text.toString(),
            "userPhone" to phoneEditText.text.toString(),
            "userAddress" to addressEditText.text.toString()
        )

        userId?.let {
            myPageApiService.updateMemberInfo(it, updatedInfo).enqueue(object :
                Callback<MemberInfoResponse> {
                override fun onResponse(call: Call<MemberInfoResponse>, response: Response<MemberInfoResponse>) {
                    if (response.isSuccessful) {


                        // Create an intent to start MyPageActivity
                        val intent = Intent(this@ModifyInfoActivity, MyPageActivity::class.java)

                        // Clear the back stack up to MainActivity
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)

                        // Finish the current activity
                        finish()
                    } else {

                    }
                }

                override fun onFailure(call: Call<MemberInfoResponse>, t: Throwable) {

                }
            })
        }
    }
}