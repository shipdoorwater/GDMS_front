package com.example.gdms_front.lucky

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.VideoView

class CustomVideoLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var videoWidth = 0
    private var videoHeight = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val videoView = getChildAt(0) as? VideoView
        if (videoView != null && videoWidth > 0 && videoHeight > 0) {
            val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)

            Log.d("CustomVideoLayout", "Parent size: $widthSpecSize x $heightSpecSize")
            Log.d("CustomVideoLayout", "Video size: $videoWidth x $videoHeight")

            val videoAspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
            val screenAspectRatio = widthSpecSize.toFloat() / heightSpecSize.toFloat()

            val scale = if (videoAspectRatio > screenAspectRatio) {
                heightSpecSize.toFloat() / videoHeight.toFloat()
            } else {
                widthSpecSize.toFloat() / videoWidth.toFloat()
            }

            val layoutWidth = (videoWidth * scale).toInt()
            val layoutHeight = (videoHeight * scale).toInt()

            Log.d("CustomVideoLayout", "Calculated layout size: $layoutWidth x $layoutHeight")

            val childWidthSpec = MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.EXACTLY)
            val childHeightSpec = MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.EXACTLY)
            videoView.measure(childWidthSpec, childHeightSpec)

            setMeasuredDimension(widthSpecSize, heightSpecSize)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun setVideoSize(width: Int, height: Int) {
        Log.d("CustomVideoLayout", "Setting video size: $width x $height")
        videoWidth = width
        videoHeight = height
        requestLayout()
    }
}