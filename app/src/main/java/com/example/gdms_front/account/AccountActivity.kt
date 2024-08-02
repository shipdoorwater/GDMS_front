package com.example.gdms_front.account

import ExpenseDecorator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.text.style.LineBackgroundSpan
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.ParseException
import com.example.gdms_front.R
import com.example.gdms_front.databinding.ActivityAccountBinding
import com.example.gdms_front.model.ExpenseData
import com.example.gdms_front.model.PayHistory
import com.example.gdms_front.model.PayHistoryResponse
import com.example.gdms_front.network.RetrofitClient
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View Binding 초기화 및 레이아웃 설정
        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        sundayDecorator = CalendarDecorators.sundayDecorator()
        saturdayDecorator = CalendarDecorators.saturdayDecorator()
        selectedMonthDecorator = CalendarDecorators.selectedMonthDecorator(
            this,
            CalendarDay.today().month
        )
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
                selectedMonthDecorator
            )

            // 월 변경 리스너 설정
            setOnMonthChangedListener { widget, date ->
                updateMonthDecorators(widget, date)
            }

            // 헤더 텍스트 스타일 설정
            setHeaderTextAppearance(R.style.CalendarWidgetHeader)

            // 범위 선택 리스너 설정 (현재는 빈 구현)
            setOnRangeSelectedListener { widget, dates -> }
        }
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
            selectedMonthDecorator
        )

        // 새로운 월의 첫 날 선택
        val clickedDay = CalendarDay.from(date.year, date.month, 1)
        widget.setDateSelected(clickedDay, true)
    }

    private fun fetchPayHistory() {
        val userId = "testtest" // 실제 사용자 ID로 변경해야 합니다
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val endDate = "29991231"//dateFormat.format(currentDate.time)
        //currentDate.add(Calendar.MONTH, -1) // 1달 전
        val startDate = "19880621" //dateFormat.format(currentDate.time)

        Log.d("달력에서 받는 값", "startDate: $startDate, endDate: $endDate")

        val apiService = RetrofitClient.apiService // RetrofitClient는 별도로 구현해야 합니다
        val call = apiService.getPayHistory(userId, startDate, endDate)

        call.enqueue(object : Callback<PayHistoryResponse> {
            override fun onResponse(call: Call<PayHistoryResponse>, response: Response<PayHistoryResponse>) {
                if (response.isSuccessful) {
                    val payHistoryList = response.body()?.message
                    if (payHistoryList != null) {
                        addPayHistoryDecorators(payHistoryList)
                    }
                    Log.d("달력", "Response: $payHistoryList")
                } else {
                    Toast.makeText(this@AccountActivity, "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PayHistoryResponse>, t: Throwable) {
                Toast.makeText(this@AccountActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addPayHistoryDecorators(payHistoryList: List<PayHistory>) {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val payHistoryMap = payHistoryList.groupBy { payHistory ->
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
                            val oldColor = paint.color
                            val oldTextSize = paint.textSize

                            paint.color = Color.BLACK
                            paint.textSize = 30f
                            paint.textAlign = Paint.Align.CENTER

                            val xPos = (left + right) / 2
                            val yPos = bottom + 40

                            val totalAmount = histories.sumBy { it.amount }
                            canvas.drawText("₩$totalAmount", xPos.toFloat(), yPos.toFloat(), paint)

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
}