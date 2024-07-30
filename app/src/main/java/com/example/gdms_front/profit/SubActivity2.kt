package com.example.gdms_front.profit

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
import com.example.gdms_front.databinding.ActivityQrPayBinding
import com.example.gdms_front.databinding.ActivitySub2Binding
import com.example.gdms_front.model.SubscribeRequest
import com.example.gdms_front.navigation_frag.ProfitFragment
import com.example.gdms_front.network.ApiService
import com.example.gdms_front.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.http.Body

class SubActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivitySub2Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySub2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // setContentView(R.layout.activity_sub2)

        val userId = intent.getStringExtra("userId")
        val packId = intent.getIntExtra("packId", 0)
        val amountPaid = intent.getIntExtra("amountPaid", 0)
        val packName = intent.getStringExtra("packName")
        val packBrief = intent.getStringExtra("packBrief")

        binding.userId.text = "userId: $userId"
        binding.packName.text = "packName : $packName"
        binding.packBrief.text = "packBrief: $packBrief"
        binding.amountPaid.text = "packBrief: $amountPaid"

        Log.d("SubActivity2", "userId: $userId, packId: $packId, amountPaid: $amountPaid, packName: $packName, packBrief: $packBrief")

        binding.buttonPay.setOnClickListener {

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.subscribe(
                        SubscribeRequest(
                            userId.toString(),
                            packId,
                            amountPaid
                        )
                    )
                    Log.d("구독결제", "Request: $response")

                    if (response.isSuccessful) {

                        Log.d("구독결제", "구독결제 successful: ${response.body()}")
                        Toast.makeText(this@SubActivity2, "구독신청 성공", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@SubActivity2,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.putExtra("FragmentToLoad", "ProfitFragment")
                        startActivity(intent)
                        finish()

                    } else {
                        // 로그인 실패 처리 (오류 메시지 표시 등)
                        Toast.makeText(this@SubActivity2, "구독신청 실패", Toast.LENGTH_SHORT).show()
                        Log.d("구독결제", "구독결제 failed: ${response.errorBody()?.string()}")
                    }


                } catch (e: Exception) {
                    // 네트워크 오류 처리
                    Toast.makeText(this@SubActivity2, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                    Log.e("구독결제", "구독결제 네트워크 failed", e)
                }
            }
        }




    }
}