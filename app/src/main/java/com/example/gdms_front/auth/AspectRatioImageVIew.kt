package com.example.gdms_front.auth

import android.content.Context
import android.util.AttributeSet
import android.view.ViewTreeObserver
import androidx.appcompat.widget.AppCompatImageView

class AspectRatioImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    init {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                screenWidth = width
                screenHeight = height
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                adjustImageAspect()
            }
        })
    }

    private fun adjustImageAspect() {
        drawable?.let { drawable ->
            val imageWidth = drawable.intrinsicWidth
            val imageHeight = drawable.intrinsicHeight

            val screenAspectRatio = screenWidth.toFloat() / screenHeight.toFloat()
            val imageAspectRatio = imageWidth.toFloat() / imageHeight.toFloat()

            if (imageAspectRatio > screenAspectRatio) {
                // 이미지가 화면보다 더 넓은 경우
                scaleType = ScaleType.CENTER_CROP
            } else {
                // 이미지가 화면보다 더 높은 경우
                scaleType = ScaleType.FIT_XY
            }
        }
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        adjustImageAspect()
    }
}