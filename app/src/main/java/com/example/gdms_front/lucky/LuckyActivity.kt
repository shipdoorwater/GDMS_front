package com.example.gdms_front.lucky


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.gdms_front.R

class LuckyActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private lateinit var videoLayout: CustomVideoLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lucky)

        videoView = findViewById(R.id.videoView)
        videoLayout = findViewById(R.id.videoLayout)

        val videoPath = "android.resource://" + packageName + "/" + R.raw.todaysluck
        videoView.setVideoPath(videoPath)

        videoView.setOnPreparedListener { mp ->
            val videoWidth = mp.videoWidth
            val videoHeight = mp.videoHeight
            Log.d("LuckyActivity", "Video prepared. Size: $videoWidth x $videoHeight")
            videoLayout.setVideoSize(videoWidth, videoHeight)

            // 비디오 반복 설정
            mp.isLooping = true

            videoView.start()
        }

        // 비디오뷰가 화면 전체를 채우도록 설정
        videoView.addOnLayoutChangeListener { _, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            Log.d("LuckyActivity", "Layout changed: [$left, $top, $right, $bottom] was [$oldLeft, $oldTop, $oldRight, $oldBottom]")
            if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                adjustVideoView()
            }
        }

        findViewById<LinearLayout>(R.id.checkMyLuckyBtn).setOnClickListener {
            val intent = Intent(this, CheckMyLuckyActivity::class.java)
            startActivity(intent)
        }
    }

    private fun adjustVideoView() {
        val parent = videoView.parent as CustomVideoLayout
        val parentWidth = parent.width
        val parentHeight = parent.height
        val videoWidth = videoView.width
        val videoHeight = videoView.height



        Log.d("LuckyActivity", "Adjusting video view. Parent: $parentWidth x $parentHeight, Video: $videoWidth x $videoHeight")

        val videoAspectRatio = videoWidth.toFloat() / videoHeight.toFloat()
        val screenAspectRatio = parentWidth.toFloat() / parentHeight.toFloat()

        val scale = if (videoAspectRatio > screenAspectRatio) {
            parentHeight.toFloat() / videoHeight.toFloat()
        } else {
            parentWidth.toFloat() / videoWidth.toFloat()
        }

        val newWidth = (videoWidth * scale).toInt()
        val newHeight = (videoHeight * scale).toInt()

        Log.d("LuckyActivity", "New size: $newWidth x $newHeight, Scale: $scale")

        val layoutParams = videoView.layoutParams
        layoutParams.width = newWidth
        layoutParams.height = newHeight
        videoView.layoutParams = layoutParams

        videoView.translationX = (parentWidth - newWidth) / 2f
        videoView.translationY = (parentHeight - newHeight) / 2f

        Log.d("LuckyActivity", "Translation: X=${videoView.translationX}, Y=${videoView.translationY}")
    }

//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        if (hasFocus) {
//            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        }
//    }
}