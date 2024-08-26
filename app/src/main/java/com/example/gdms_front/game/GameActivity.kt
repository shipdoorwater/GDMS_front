package com.example.gdms_front.game

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.gdms_front.R

class GameActivity : AppCompatActivity() {
    private lateinit var gameView: GameView
    private lateinit var scoreTextView: TextView
    private lateinit var gameOverLayout: ConstraintLayout
    private lateinit var finalScoreTextView: TextView
    private lateinit var restartButton: Button
    private lateinit var exitButton: Button
    private lateinit var highScoreTextView: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var lastUpdateTime = System.nanoTime()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameView = findViewById(R.id.gameView)
        scoreTextView = findViewById(R.id.scoreTextView)
        gameOverLayout = findViewById(R.id.gameOverLayout)
        finalScoreTextView = findViewById(R.id.finalScoreTextView)
        restartButton = findViewById(R.id.restartButton)
        exitButton = findViewById(R.id.exitButton)
        highScoreTextView = findViewById(R.id.highScoreTextView)

        gameView.onGameOver = { showGameOver() }
        restartButton.setOnClickListener { restartGame() }
        exitButton.setOnClickListener { finish() }

        startGame()

        val highScore = loadHighScore()
        highScoreTextView.text = "High Score: $highScore"
    }

    private fun startGame() {
        gameView.reset()
        gameOverLayout.visibility = View.GONE
        lastUpdateTime = System.nanoTime()
        updateGame()
    }

    private fun updateGame() {
        val currentTime = System.nanoTime()
        val deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000f
        lastUpdateTime = currentTime

        gameView.update(deltaTime)
        scoreTextView.text = "Score: ${gameView.score}"

        if (!gameView.isGameOver) {
            handler.postDelayed({ updateGame() }, 16) // 약 60 FPS
        }
    }

    private fun showGameOver() {
        gameOverLayout.visibility = View.VISIBLE
        finalScoreTextView.text = "Final Score: ${gameView.score}"
        val highScore = loadHighScore()

        // 최고 점수를 항상 표시
        highScoreTextView.text = "High Score: $highScore"

        // 현재 점수가 최고 기록을 갱신한 경우 추가 메시지 표시
        if (gameView.score > highScore) {
            saveHighScore(gameView.score)  // 현재 점수가 최고 기록보다 높으면 갱신
            highScoreTextView.text = "New High Score: ${gameView.score}!"
            highScoreTextView.append("(New!)")  // 최고 기록 갱신 메시지 추가
        }

        saveScore(gameView.score)  // 게임 종료 시 마지막 점수 저장

    }

    private fun saveScore(score: Int) {
        val sharedPreferences = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("last_score", score)
        editor.apply()
    }


    private fun saveHighScore(score: Int) {
        val sharedPreferences = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("high_score", score)
        editor.apply()
    }

    private fun loadHighScore(): Int {
        val sharedPreferences = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("high_score", 0)
    }

    private fun restartGame() {
        startGame()
    }
}
