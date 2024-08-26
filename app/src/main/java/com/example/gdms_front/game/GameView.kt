package com.example.gdms_front.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.gdms_front.R
import kotlin.random.Random

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val playerBitmap1: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.perle)
    private val poopBitmap1: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bomb)


    private val playerBitmap: Bitmap = Bitmap.createScaledBitmap(playerBitmap1, 120, 120, true)
    private val poopBitmap: Bitmap = Bitmap.createScaledBitmap(poopBitmap1, 100, 100, true)

    private val player = RectF(100f, 100f, 100f + playerBitmap.width, 100f + playerBitmap.height)
    private val poops = mutableListOf<Poop>()
//    private val poopPaint = Paint().apply { color = Color.BLACK }
//    private val playerPaint = Paint().apply { color = Color.BLUE }
    private val random = Random
    private var poopSpeed = 5f
    private var gameTime = 0f

    var score = 0
        private set

    var isGameOver = false
        private set

    var onGameOver: (() -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(playerBitmap, player.left, player.top, null)
        poops.forEach { poop ->
            canvas.drawBitmap(poopBitmap, poop.x, poop.y, null)
        }

    }

    fun update(deltaTime: Float) {
        if (!isGameOver) {
            gameTime += deltaTime
            poopSpeed = 5f + (gameTime * 10 / 10f) // 시간이 지날수록 속도 증가
            updatePoops()
            checkCollisions()
            invalidate()
        }
    }

    private fun updatePoops() {
        poops.forEach { it.y += poopSpeed }
        poops.removeAll { poop ->
            if (poop.y > height) {
                score += 10
                true
            } else {
                false
            }
        }
        if (random.nextFloat() < 0.05f) {
            createPoop()
        }
    }

    private fun createPoop() {
        val x = random.nextFloat() * (width - poopBitmap.width)
        poops.add(Poop(x, -poopBitmap.height.toFloat(), poopBitmap.width.toFloat()))

    }

    private fun checkCollisions() {

        val playerRect = RectF(player.left + 10, player.top + 10, player.right - 10, player.bottom - 10)
        poops.forEach { poop ->
            val poopRect = RectF(poop.x + 10, poop.y + 10, poop.x + poopBitmap.width - 10, poop.y + poopBitmap.height - 10)
            if (RectF.intersects(playerRect, poopRect)) {
                isGameOver = true
                onGameOver?.invoke()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isGameOver && event.action == MotionEvent.ACTION_MOVE) {
            player.offsetTo(event.x - player.width() / 2, height - player.height() - 50f)
            invalidate()
        }
        return true
    }

    fun reset() {
        score = 0
        isGameOver = false
        poops.clear()
        poopSpeed = 5f
        gameTime = 0f
        player.offsetTo(width / 2f - player.width() / 2, height - player.height() - 50f)
        invalidate()
    }
}
// 푸쉬용