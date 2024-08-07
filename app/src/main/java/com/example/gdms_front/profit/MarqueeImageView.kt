package com.example.gdms_front.profit

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.example.gdms_front.R

class MarqueeImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val image: Drawable = ContextCompat.getDrawable(context, R.drawable.marquee2)!!
    private var scrollPos = 0
    private var animator: ValueAnimator? = null

    init {
        image.setBounds(0, 0, image.intrinsicWidth, image.intrinsicHeight)
        startAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        val width = image.intrinsicWidth
        canvas.translate(-scrollPos.toFloat(), 0f)
        image.draw(canvas)
        canvas.translate(width.toFloat(), 0f)
        image.draw(canvas)
        canvas.restore()
    }

    private fun startAnimation() {
        animator = ValueAnimator.ofInt(0, image.intrinsicWidth).apply {
            duration = 10000 // 애니메이션 지속 시간 (10초)
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator() // 일정한 속도로 애니메이션
            addUpdateListener {
                scrollPos = it.animatedValue as Int
                invalidate()
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.pause() // 화면을 떠날 때 애니메이션을 일시 중지합니다.
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        animator?.resume() // 화면에 다시 나타날 때 애니메이션을 재개합니다.
    }
}
