import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import com.example.gdms_front.model.ExpenseData
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class ExpenseDecorator(private val expenses: List<ExpenseData>) : DayViewDecorator {
    private val expenseMap = expenses.associateBy { it.date }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return expenseMap.containsKey(day)
    }

    override fun decorate(view: DayViewFacade) {
        val expense = expenseMap[CalendarDay.today()] // This is a workaround, ideally we should pass the current day
        if (expense != null) {
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
                    val yPos = bottom + 40 // Adjust this value to position the text

                    canvas.drawText("₩${expense.amount.toInt()}", xPos.toFloat(), yPos.toFloat(), paint)

                    paint.color = oldColor
                    paint.textSize = oldTextSize
                }
            })
        }
    }
}