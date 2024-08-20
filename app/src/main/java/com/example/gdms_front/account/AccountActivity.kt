package com.example.gdms_front.account

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.icu.text.NumberFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.text.style.LineBackgroundSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.ParseException
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gdms_front.R
import com.example.gdms_front.adapter.CategoryAdapter
import com.example.gdms_front.adapter.PayHistoryAdapter
import com.example.gdms_front.databinding.ActivityAccountBinding
import com.example.gdms_front.model.PayHistory
import com.example.gdms_front.model.PayHistoryResponse
import com.example.gdms_front.network.RetrofitClient
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class AccountActivity : AppCompatActivity() {

    // View Binding 객체
    private lateinit var binding: ActivityAccountBinding

    // 캘린더 데코레이터 객체들
    private lateinit var dayDecorator: DayViewDecorator
    private lateinit var todayDecorator: DayViewDecorator
    private lateinit var selectedMonthDecorator: DayViewDecorator
    private lateinit var sundayDecorator: DayViewDecorator
    private lateinit var saturdayDecorator: DayViewDecorator
    private lateinit var selectedDateDecorator: DayViewDecorator

    // 지출 내역 데이터를 저장할 전역 변수
    private var payHistoryMap: Map<CalendarDay, List<PayHistory>> = emptyMap()
    private lateinit var payHistoryAdapter: PayHistoryAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    // PieChart 객체
    private lateinit var pieChart: PieChart

    // 현재 선택된 월
    private var selectedMonth: CalendarDay = CalendarDay.today()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View Binding 초기화 및 레이아웃 설정
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 제목과 더보기 버튼을 항상 보이도록 설정
        binding.tvTitle.visibility = View.VISIBLE
        binding.btnMore.visibility = View.VISIBLE

        // PieChart 초기화
        pieChart = binding.pieChart
        setupPieChart()

        // 데코레이터 초기화 및 뷰 설정
        fetchPayHistory()
        initDecorators()
        initView()
    }

    /**
     * 캘린더 데코레이터 초기화
     */
    private fun initDecorators() {
        dayDecorator = CalendarDecorators.dayDecorator(this)
        todayDecorator = CalendarDecorators.todayDecorator(this)
        selectedDateDecorator = CalendarDecorators.selectedDateDecorator(this, CalendarDay.today())
        sundayDecorator = CalendarDecorators.sundayDecorator()
        saturdayDecorator = CalendarDecorators.saturdayDecorator()
        selectedMonthDecorator =
            CalendarDecorators.selectedMonthDecorator(this, CalendarDay.today().month)
    }

    /**
     * 뷰 초기화 및 설정
     */
    private fun initView() = with(binding) {
        with(calendarView) {

            // 데코레이터 추가
            addDecorators(
                todayDecorator,
                sundayDecorator,
                saturdayDecorator,
                selectedMonthDecorator,
                selectedDateDecorator
            )

            // 월 변경 리스너 설정
            setOnMonthChangedListener { widget, date ->
                selectedMonth = date
                updateMonthDecorators(widget, date)
                updatePieChart(date)
                updateCategoryList(date)
            }

            // 날짜 선택 리스너 설정
            setOnDateChangedListener { widget, date, selected ->
                if (selected) {

                    // 기존 데코레이터 제거
                    removeDecorator(selectedDateDecorator)

                    // 새로운 선택된 날짜로 데코레이터 업데이트
                    selectedDateDecorator = CalendarDecorators.selectedDateDecorator(this@AccountActivity, date)

                    // 새 데코레이터 추가
                    addDecorator(selectedDateDecorator)

                    // 캘린더 뷰 갱신
                    widget.invalidateDecorators()
                    displayPayHistoryDetails(date)
                }
            }

            // 헤더 텍스트 스타일 설정
            setHeaderTextAppearance(R.style.CalendarWidgetHeader)

            // 범위 선택 리스너 설정 (현재는 빈 구현)
            setOnRangeSelectedListener { widget, dates -> }
        }
        recyclerView.layoutManager = LinearLayoutManager(this@AccountActivity)
        payHistoryAdapter = PayHistoryAdapter(emptyList(), false)
        recyclerView.adapter = payHistoryAdapter

        btnMore.setOnClickListener {
            // "더보기" 버튼 클릭 시 모든 지출 내역을 보여주는 코드 작성
            displayAllPayHistories()
        }

        categoryRecyclerView.layoutManager = LinearLayoutManager(this@AccountActivity)
        categoryAdapter = CategoryAdapter(emptyList(), emptyMap()) { category ->
            displayCategoryDetails(category)
        }
        categoryRecyclerView.adapter = categoryAdapter
    }

    /**
     * 월이 변경될 때 데코레이터 업데이트
     * @param widget 캘린더 뷰 객체
     * @param date 변경된 월의 날짜
     */
    private fun updateMonthDecorators(widget: MaterialCalendarView, date: CalendarDay) {
        // 기존 선택 및 데코레이터 초기화
        widget.clearSelection()
        widget.removeDecorators()
        widget.invalidateDecorators()

        // 새로운 월에 대한 데코레이터 생성 및 추가
        selectedMonthDecorator = CalendarDecorators.selectedMonthDecorator(this, date.month)
        widget.addDecorators(
            todayDecorator,
            sundayDecorator,
            saturdayDecorator,
            selectedMonthDecorator,
            selectedDateDecorator
        )

        // 지출 내역 데코레이터 추가
        addPayHistoryDecorators(payHistoryMap)

        // 새로운 월의 첫 날 선택 (구려서 꺼놓음)
        // val clickedDay = CalendarDay.from(date.year, date.month, 1)
        // widget.setDateSelected(clickedDay, true)
    }

    private fun fetchPayHistory() {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("token", null).toString()
        val endDate = "29991231"
        val startDate = "19880621"

        Log.d("달력에서 받는 값", "startDate: $startDate, endDate: $endDate")

        val apiService = RetrofitClient.apiService
        val call = apiService.getPayHistory(userId, startDate, endDate)

        call.enqueue(object : Callback<PayHistoryResponse> {
            override fun onResponse(
                call: Call<PayHistoryResponse>,
                response: Response<PayHistoryResponse>
            ) {
                if (response.isSuccessful) {
                    val payHistoryList = response.body()?.message
                    if (payHistoryList != null) {
                        payHistoryMap = convertPayHistoryListToMap(payHistoryList)
                        addPayHistoryDecorators(payHistoryMap)
                        updatePieChart(CalendarDay.today())
                        updateCategoryList(CalendarDay.today())
                    }
                    Log.d("달력", "Response: $payHistoryList")
                } else {
                    Toast.makeText(this@AccountActivity, "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<PayHistoryResponse>, t: Throwable) {
                Toast.makeText(this@AccountActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 지출 내역 리스트를 Map으로 변환하는 함수
     * @param payHistoryList 지출 내역 리스트
     * @return CalendarDay를 키로 하고 PayHistory 리스트를 값으로 하는 Map
     */
    private fun convertPayHistoryListToMap(payHistoryList: List<PayHistory>): Map<CalendarDay, List<PayHistory>> {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return payHistoryList.groupBy { payHistory ->
            try {
                val date = dateFormat.parse(payHistory.payDate)
                val calendar = Calendar.getInstance()
                calendar.time = date
                CalendarDay.from(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            } catch (e: ParseException) {
                null
            }
        }.filterKeys { it != null } as Map<CalendarDay, List<PayHistory>>
    }

    /**
     * 지출 내역 데코레이터를 추가하는 함수
     * @param payHistoryMap 지출 내역 데이터가 담긴 Map
     */
    private fun addPayHistoryDecorators(payHistoryMap: Map<CalendarDay, List<PayHistory>>) {
        payHistoryMap.forEach { (day, histories) ->
            val decorator = object : DayViewDecorator {
                override fun shouldDecorate(d: CalendarDay): Boolean {
                    return d == day
                }

                override fun decorate(view: DayViewFacade) {
                    view.addSpan(object : LineBackgroundSpan {
                        override fun drawBackground(
                            canvas: Canvas,
                            paint: Paint,
                            left: Int,
                            right: Int,
                            top: Int,
                            baseline: Int,
                            bottom: Int,
                            text: CharSequence,
                            start: Int,
                            end: Int,
                            lnum: Int
                        ) {
                            // 기존 색상과 텍스트 크기를 저장
                            val oldColor = paint.color
                            val oldTextSize = paint.textSize

                            // 색상과 텍스트 크기 설정
                            paint.color = Color.RED
                            paint.textSize = 30f
                            paint.textAlign = Paint.Align.CENTER

                            // 텍스트 위치 계산
                            val xPos = (left + right) / 2
                            val yPos = bottom + 40

                            // 지출 금액 합산 및 포맷팅
                            val totalAmount = histories.sumBy { it.amount }
                            val formattedAmount =
                                NumberFormat.getNumberInstance(Locale.getDefault())
                                    .format(totalAmount)
                            canvas.drawText(
                                "₩$formattedAmount",
                                xPos.toFloat(),
                                yPos.toFloat(),
                                paint
                            )

                            // 기존 색상과 텍스트 크기 복원
                            paint.color = oldColor
                            paint.textSize = oldTextSize
                        }
                    })
                }
            }
            binding.calendarView.addDecorator(decorator)
        }
        binding.calendarView.invalidateDecorators()
    }

    private fun displayPayHistoryDetails(date: CalendarDay) {
        val payHistories = payHistoryMap[date] ?: emptyList()
        payHistoryAdapter.updateData(payHistories, false)
        binding.tvTitle.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.VISIBLE
        binding.btnMore.visibility = View.VISIBLE
    }

    private fun displayAllPayHistories() {
        val allPayHistories = payHistoryMap.values.flatten().sortedByDescending { it.payDate }
        payHistoryAdapter.updateData(allPayHistories, true)
    }

    private fun displayCategoryDetails(category: String) {
        val intent = Intent(this, CategoryDetailActivity::class.java)
        intent.putExtra("storeCode", getStoreCode(category))
        intent.putExtra("selectedMonth", selectedMonth.year * 100 + selectedMonth.month) // YYYYMM 형식으로 전달
        intent.putExtra("category", category) // 카테고리 이름 전달
        startActivity(intent)
    }

    private fun setupPieChart() {
        pieChart.apply {
            description.isEnabled = false
            isRotationEnabled = false
            setUsePercentValues(false)
            setDrawEntryLabels(false)
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            centerText = "지출 카테고리"
            setCenterTextSize(18f)
            legend.isEnabled = true
        }
    }

    private fun updatePieChart(date: CalendarDay) {
        val selectedMonth = date.month
        val selectedYear = date.year

        val categoryTotals = mutableMapOf<String, Int>()
        var totalAmount = 0

        payHistoryMap.filterKeys { it.year == selectedYear && it.month == selectedMonth }
            .values.flatten()
            .forEach { payHistory ->
                val amount = payHistory.amount
                categoryTotals[payHistory.storeCode.toString()] = categoryTotals.getOrDefault(payHistory.storeCode.toString(), 0) + amount
                totalAmount += amount
            }

        // 디버깅 출력을 추가하여 값을 확인합니다.
        categoryTotals.forEach { (category, total) ->
            Log.d("Category Total", "Category: $category, Total: $total")
        }

        val entries = categoryTotals.map { (storeCode, total) ->
            val categoryName = getCategoryName(storeCode)
            val percentage = total.toFloat() / totalAmount * 100
            PieEntry(total.toFloat(), "$categoryName: ${String.format("%.1f", percentage)}%")
        }

        val dataSet = PieDataSet(entries, "카테고리별 지출")

        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 25f // 텍스트 크기를 조정했습니다.
        dataSet.valueTextColor = Color.BLACK

        // ValueFormatter를 사용하여 Total 값을 표시합니다.
        val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return formatter.format(value.toDouble())
            }
        }

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.setUsePercentValues(false) // 퍼센트 값 대신 실제 값을 사용합니다.
        pieChart.setDrawEntryLabels(true) // 엔트리 레이블을 표시합니다.
        pieChart.invalidate() // Refresh the chart
    }

    private fun getCategoryName(storeCode: String): String {
        return when (storeCode) {
            "1" -> "음식점"
            "2" -> "금융상품"
            "3" -> "면세점/해외승인"
            "4" -> "편의점"
            "5" -> "베이커리"
            "6" -> "서점"
            "7" -> "택시,주유"
            "8" -> "인강"
            "9" -> "술집"
            "10" -> "꽃집"
            else -> "기타"
        }
    }

    private fun getStoreCode(category: String): String {
        return when (category) {
            "음식점" -> "1"
            "금융상품" -> "2"
            "면세점/해외승인" -> "3"
            "편의점" -> "4"
            "베이커리" -> "5"
            "서점" -> "6"
            "택시,주유" -> "7"
            "인강" -> "8"
            "술집" -> "9"
            "꽃집" -> "10"
            else -> "11"
        }
    }

    private fun updateCategoryList(date: CalendarDay) {
        val selectedMonth = date.month
        val selectedYear = date.year

        val categoryTotals = mutableMapOf<String, Int>()

        payHistoryMap.filterKeys { it.year == selectedYear && it.month == selectedMonth }
            .values.flatten()
            .forEach { payHistory ->
                val amount = payHistory.amount
                categoryTotals[payHistory.storeCode.toString()] = categoryTotals.getOrDefault(payHistory.storeCode.toString(), 0) + amount
            }

        val categories = categoryTotals.map { (storeCode, total) ->
            val categoryName = getCategoryName(storeCode)
            categoryName to total
        }

        categoryAdapter = CategoryAdapter(categories, payHistoryMap) { category ->
            displayCategoryDetails(category)
        }
        binding.categoryRecyclerView.adapter = categoryAdapter

        // 데이터가 있을 때만 RecyclerView와 TextView를 보이도록 설정
        if (categories.isNotEmpty()) {
            binding.categoryRecyclerView.visibility = View.VISIBLE
            //binding.tvCategoryTitle.visibility = View.VISIBLE
        } else {
            binding.categoryRecyclerView.visibility = View.GONE
           // binding.tvCategoryTitle.visibility = View.GONE
        }
    }
}
