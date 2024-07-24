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
        val bizNo = intent.getStringExtra("QR_CODE_VALUE")

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
                            Toast.makeText(this@QrPayActivity, "Payment success", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@QrPayActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@QrPayActivity, "Payment failed", Toast.LENGTH_SHORT).show()
                            Log.e("QrPayActivity123", "Payment failed: ${response.errorBody()?.string()}")
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@QrPayActivity, "Network error occurred", Toast.LENGTH_SHORT).show()
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