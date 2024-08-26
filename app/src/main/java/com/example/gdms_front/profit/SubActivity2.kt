package com.example.gdms_front.profit

import android.content.Intent
import android.icu.text.NumberFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gdms_front.R
import com.example.gdms_front.databinding.ActivitySub2Binding
import com.example.gdms_front.model.MemberInfoResponse
import com.example.gdms_front.model.SubscribeRequest
import com.example.gdms_front.network.RetrofitClient
import com.example.gdms_front.network.RetrofitClient.myPageApiService
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

        getMemberInfo(userId.toString())

        //binding.userId.text = "$userId 님\n구독 결제정보를 확인해 주세요"
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

                        }
                        
                        
                        // 나가서 메인 엑티비티로 가는데 그 다음 프레그먼트 어디로 갈지 정해줌(일단 기능 보류)
//                        val intent = Intent(this@SubActivity2,MainActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                        intent.putExtra("FragmentToLoad", "ProfitFragment")
//                        startActivity(intent)
//                        finish()

                    } else {
                        // 로그인 실패 처리 (오류 메시지 표시 등)

                        Log.d("구독결제", "구독결제 failed: ${response.errorBody()?.string()}")
                    }


                } catch (e: Exception) {
                    // 네트워크 오류 처리

                    Log.e("구독결제", "구독결제 네트워크 failed", e)
                }
            }
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

                        binding.userId.text = "${memberInfo.userName} 님\n구독 결제정보를 확인해 주세요"
                        //view.findViewById<TextView>(R.id.userName).text = "${memberInfo.userName}님 안녕하세요"
                        Log.d("AllMenuFragment", "Member info retrieved successfully: ${memberInfo.userName}")
                    } else {
                        Log.e("AllMenuFragment", "Member info is null")
                    }
                } else if (response.code() == 404) {
                    Log.e("AllMenuFragment", "404 Error: User not found")
                } else {
                    Log.e("AllMenuFragment", "Error: ${response.code()}, ${response.message()}")
                }
            }

            override fun onFailure(call: Call<MemberInfoResponse>, t: Throwable) {
                Log.e("AllMenuFragment", "Network failure: ${t.message}", t)
            }
        })
    }


}