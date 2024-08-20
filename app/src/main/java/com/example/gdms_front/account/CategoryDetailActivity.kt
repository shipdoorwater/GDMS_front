package com.example.gdms_front.account

import android.content.Context
import android.icu.text.NumberFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gdms_front.R
import com.example.gdms_front.adapter.DateGroupedPayHistoryAdapter
import com.example.gdms_front.databinding.ActivityCategoryDetailBinding
import com.example.gdms_front.model.PayHistory
import com.example.gdms_front.model.PayHistoryResponse
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class CategoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryDetailBinding
    private lateinit var payHistoryAdapter: DateGroupedPayHistoryAdapter
    private var selectedMonth: Int = 0
    private var category: String = ""
    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 storeCode 값을 받아옴
        val storeCode = intent.getStringExtra("storeCode")
        selectedMonth = intent.getIntExtra("selectedMonth", 0)
        category = intent.getStringExtra("category") ?: ""

        // RecyclerView 설정
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        payHistoryAdapter = DateGroupedPayHistoryAdapter(emptyMap())
        binding.recyclerView.adapter = payHistoryAdapter

        // storeCode에 해당하는 지출 내역을 가져옴
        if (storeCode != null) {
            fetchPayHistory(storeCode)
        } else {
            Toast.makeText(this, "storeCode를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPayHistory(storeCode: String) {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("token", null).toString()
        val apiService = RetrofitClient.apiService
        val call = apiService.getPayHistory(userId, "19880621", "29991231")

        call.enqueue(object : Callback<PayHistoryResponse> {
            override fun onResponse(
                call: Call<PayHistoryResponse>,
                response: Response<PayHistoryResponse>
            ) {
                if (response.isSuccessful) {
                    val payHistoryList = response.body()?.message
                    if (payHistoryList != null) {
                        // 선택된 월에 해당하는 데이터만 필터링
                        val filteredList = payHistoryList.filter {
                            it.storeCode.toString() == storeCode && isSelectedMonth(it.payDate)
                        }
                        val groupedMap = filteredList.groupBy { it.payDate }
                        payHistoryAdapter.updateData(groupedMap)
                        updateSummary(filteredList)
                    }
                } else {
                    Toast.makeText(this@CategoryDetailActivity, "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<PayHistoryResponse>, t: Throwable) {
                Toast.makeText(this@CategoryDetailActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateSummary(filteredList: List<PayHistory>) {
        val totalAmount = filteredList.sumBy { it.amount }
        val month = selectedMonth % 100
        val year = selectedMonth / 100
        val formattedAmount = numberFormat.format(totalAmount)
        val summaryText = "${year}년 ${month}월\n${category}에 쓴 돈은\n${formattedAmount}원 입니다."
        binding.summaryTextView.text = summaryText

        val iconResId = getCategoryIcon(category)
        binding.categoryIcon.setImageResource(iconResId)
    }

    private fun getCategoryIcon(category: String): Int {
        return when (category) {

            "음식점" -> R.drawable.payhistory_icon_1_food
            "금융상품" -> R.drawable.payhistory_icon_2_finance
            "면세점/해외승인" -> R.drawable.payhistory_icon_3_taxfree
            "편의점" -> R.drawable.payhistory_icon_4_cv
            "베이커리" -> R.drawable.payhistory_icon_5_bakery
            "서점" -> R.drawable.payhistory_icon_6_book
            "택시,주유" -> R.drawable.payhistory_icon_7_taxi
            "인강" -> R.drawable.payhistory_icon_8_lecture
            "술집" -> R.drawable.payhistory_icon_9_alchol
            "꽃집" -> R.drawable.payhistory_icon_10_flower
            else -> R.drawable.payhistory_icon_11_etc
        }
    }

    private fun isSelectedMonth(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date

        val yearMonth = calendar.get(Calendar.YEAR) * 100 + (calendar.get(Calendar.MONTH) + 1)
        return yearMonth == selectedMonth
    }
}
