package com.example.gdms_front.profit

import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.gdms_front.MainActivity
import com.example.gdms_front.R
import com.example.gdms_front.databinding.ActivityQrPayBinding
import com.example.gdms_front.databinding.ActivitySub2Binding
import com.example.gdms_front.model.SubscribeRequest
import com.example.gdms_front.model.SubscribeResponse
import com.example.gdms_front.navigation_frag.ProfitFragment
import com.example.gdms_front.network.ApiService
import com.example.gdms_front.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.http.Body
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class SubActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivitySub2Binding


    @RequiresApi(Build.VERSION_CODES.O)
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

        // 현재 날짜 가져오기
        val today = LocalDate.now()
        // 한 달 뒤의 날짜 계산
        val oneMonthLater = today.plusMonths(1)
        // 한 달 뒤의 다음 날 계산
        val oneMonthAndOneDayLater = oneMonthLater.plusDays(1)
        // 날짜를 문자열로 포맷팅
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

        val todayStr = today.format(formatter)
        val oneMonthLaterStr = oneMonthLater.format(formatter)
        val oneMonthAndOneDayLaterStr = oneMonthAndOneDayLater.format(formatter)

        // 금액 포맷팅 (쉼표 추가)
        val formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(amountPaid)



        binding.userId.text = "$userId 님\n구독 결제정보를 확인해 주세요"
        binding.packName.text = "$packName 팩 월 정기구독"

        binding.packBrief.text =  "#구독기간      |  $todayStr ~ $oneMonthLaterStr"
        binding.packBrief2.text = "#다음결제일  |  $oneMonthAndOneDayLaterStr"

        binding.amountPaid.text = "$formattedAmount 원"

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

                        val rawRedirectUrl = response.body()
                        Log.d("구독결제", "Redirect URL: $rawRedirectUrl")
                        val redirectUrl = rawRedirectUrl?.redirectUrl
                        Log.d("구독결제", "Redirect URL: $redirectUrl")
                        val tid = rawRedirectUrl?.tid


                        if (redirectUrl != null) {

                            val intent = Intent(this@SubActivity2, KakaoPayActivity::class.java)
                            intent.putExtra("redirectUrl", redirectUrl)
                            intent.putExtra("userId", userId)
                            intent.putExtra("packId", packId)
                            intent.putExtra("amountPaid", amountPaid)
                            intent.putExtra("packName", packName)
                            intent.putExtra("packBrief", packBrief)
                            intent.putExtra("tid", tid)
                            startActivity(intent)

                        } else {
                            Toast.makeText(this@SubActivity2, "Redirect URL not found", Toast.LENGTH_SHORT).show()
                        }
                        
                        
                        // 나가서 메인 엑티비티로 가는데 그 다음 프레그먼트 어디로 갈지 정해줌(일단 기능 보류)
//                        val intent = Intent(this@SubActivity2,MainActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        intent.putExtra("FragmentToLoad", "ProfitFragment")
//                        startActivity(intent)
//                        finish()

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