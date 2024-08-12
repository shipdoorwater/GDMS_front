package com.example.gdms_front.qr_pay

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
import com.example.gdms_front.auth.JoinSucActivity
import com.example.gdms_front.databinding.ActivityQrPayBinding
import com.example.gdms_front.model.JoinRequest
import com.example.gdms_front.model.PayRequest
import com.example.gdms_front.network.RetrofitClient
import kotlinx.coroutines.launch

class QrPayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrPayBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrPayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("token", null)
        //val bizNo = intent.getStringExtra("QR_CODE_VALUE")

        val qrCodeValue = intent.getStringExtra("QR_CODE_VALUE")


        //QR코드는 qr-code-generator.com 에서
        //shopName : 버거킹 강남대로
        //bizNo : 000000000
        //adrs : 서울시 영등포구 여의도동
        // 이 양식으로 줄바꾸기 지켜서 작성해야 함

        if (qrCodeValue != null) {
            val qrData = parseQrCode(qrCodeValue)
            val shopName = qrData["shopName"]
            val bizNo = qrData["bizNo"]
            val adrs = qrData["adrs"]

            Log.d("QrPayActivity123", "shopName: $shopName, bizNo: $bizNo, adrs: $adrs")

            binding.textViewShopName.text = "상호 : $shopName"
            binding.textViewBizNo.text = "사업자 번호: $bizNo"
            binding.textViewAdrs.text = "주소: $adrs"




            binding.buttonPay.setOnClickListener {
                val amountStr = binding.editTextAmount.text.toString()
                val payment = amountStr.toIntOrNull()

                Log.d("QrPayActivity123", "userId: $userId, bizNo: $bizNo, payment: $payment")

                if (userId != null && bizNo != null && payment != null) {
                    val payRequest = PayRequest(userId, bizNo, payment)

                    lifecycleScope.launch {
                        try {
                            val response = RetrofitClient.apiService.pay(payRequest)
                            Log.d("QrPayActivity123", "Request: $payRequest")
                            Log.d("QrPayActivity123", "Response: ${response.code()}")
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@QrPayActivity,
                                    "Payment success",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // 나가서 메인 엑티비티로 가는데 그 다음 프레그먼트 어디로 갈지 정해줌
                                val intent = Intent(this@QrPayActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.putExtra("FragmentToLoad", "PayFragment")
                                startActivity(intent)
                                finish()

//                                val intent = Intent(this@QrPayActivity, MainActivity::class.java)
//                                startActivity(intent)
//                                finish()
                            } else {
                                Toast.makeText(
                                    this@QrPayActivity,
                                    "Payment failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e(
                                    "QrPayActivity123",
                                    "Payment failed: ${response.errorBody()?.string()}"
                                )
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@QrPayActivity,
                                "Network error occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("QrPayActivity123", "Payment failed", e)
                        }
                    }
                } else {
                    Toast.makeText(this@QrPayActivity, "Invalid input", Toast.LENGTH_SHORT).show()
                    if (payment == null) {
                        binding.editTextAmount.error = "Invalid amount"
                    }
                }
            }
        }
    }

    private fun parseQrCode(qrCodeValue: String): Map<String, String> {
        val qrDataMap = mutableMapOf<String, String>()
        val lines = qrCodeValue.split("\n")

        for (line in lines) {
            val parts = line.split(" : ")
            if (parts.size == 2) {
                val key = parts[0].trim()
                val value = parts[1].trim()
                qrDataMap[key] = value
            }
        }

        return qrDataMap
    }
}