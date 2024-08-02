package com.example.gdms_front.account

import android.content.Context
import android.graphics.Color
import android.icu.util.Calendar
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.example.gdms_front.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.time.LocalDate

object CalendarDecorators {
    /**
     * 날짜를 표시하는 데 사용되는 요소를 정의하기 위한 함수
     * @param context 리소스에 액세스하기 위해 사용되는 컨텍스트
     * @return DayViewDecorator 객체
     */
    fun dayDecorator(context: Context): DayViewDecorator {
        return object : DayViewDecorator {
            private val drawable = ContextCompat.getDrawable(context, R.drawable.img_nav_black_900) //R.drawable.calendar_selector)
            override fun shouldDecorate(day: CalendarDay): Boolean = true
            override fun decorate(view: DayViewFacade) {
                view.setSelectionDrawable(drawable!!)
            }
        }
    }

    /**
     * 현재 날짜를 다른 날짜와 구별하기 위해 스타일이나 색상을 적용하기 위한 함수
     * @param context 리소스에 액세스하기 위해 사용되는 컨텍스트
     * @return DayViewDecorator 객체
     */
    fun todayDecorator(context: Context): DayViewDecorator {
        return object : DayViewDecorator {
            private val backgroundDrawable =
                ContextCompat.getDrawable(context, R.drawable.img_material_symbols_light_search) //R.drawable.calendar_circle_today)
            private val today = CalendarDay.today()

            override fun shouldDecorate(day: CalendarDay?): Boolean = day == today

            override fun decorate(view: DayViewFacade?) {
                view?.apply {
                    setBackgroundDrawable(backgroundDrawable!!)
                    addSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                context,
                                R.color.main_color
                            )
                        )
                    )
                }
            }
        }
    }

    /**
     * 현재 선택된 날 이외의 다른 달의 날짜의 모양을 변경하기 위한 함수
     * @param context 리소스에 액세스하기 위해 사용되는 컨텍스트
     * @param selectedMonth 현재 선택 된 달
     * @return DayViewDecorator 객체
     */
    fun selectedMonthDecorator(context: Context, selectedMonth: Int): DayViewDecorator {
        return object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean = day.month != selectedMonth
            override fun decorate(view: DayViewFacade) {
                view.addSpan(
                    ForegroundColorSpan(
                        ContextCompat.getColor(
                            context,
                            R.color.colorAccent //enabled_date_color
                        )
                    )
                )
            }
        }
    }

    /**
     * 일요일을 강조하는 데코레이터를 생성하기 위한 함수
     * @return DayViewDecorator 객체
     */
    fun sundayDecorator(): DayViewDecorator {
        return object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val calendar = Calendar.getInstance()
                calendar.set(day.year, day.month - 1, day.day)
                return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(Color.BLACK))
            }
        }
    }

    /**
     * 토요일을 강조하는 데코레이터를 생성하기 위한 함수
     * @return DayViewDecorator 객체
     */
    fun saturdayDecorator(): DayViewDecorator {
        return object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                val calendar = Calendar.getInstance()
                calendar.set(day.year, day.month - 1, day.day)
                return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(ForegroundColorSpan(Color.BLACK))
            }
        }
    }

    /**
     * 이벤트가 있는 날짜를 표시하는 데코레이터를 생성하기 위한 함수
     * @param context 리소스에 액세스하기 위해 사용되는 컨텍스트
     * @param scheduleList 이벤트 날짜를 포함하는 스케줄 목록
     * @return DayViewDecorator 객체
     */

}