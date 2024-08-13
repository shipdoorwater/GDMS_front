package com.example.gdms_front.lucky

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gdms_front.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.random.Random

class CheckMyLuckyActivity : AppCompatActivity() {

    private lateinit var luckyPhraseTextView: TextView
    private lateinit var luckyNumberTextView: TextView

    // 수정
    private val luckyPhrases = listOf(
        "오늘은 행운의 날입니다!",
        "좋은 일이 곧 일어날 거예요.",
        "당신의 노력이 곧 결실을 맺을 것입니다.",
        "긍정적인 마음가짐이 행운을 불러옵니다.",
        "새로운 기회가 당신을 기다리고 있어요.",
        "오늘 하루도 힘내세요!",
        "당신의 꿈은 반드시 이루어질 거예요.",
        "행운은 준비된 자에게 찾아옵니다.",
        "오늘 하루가 특별할 거예요.",
        "당신의 미래는 밝습니다!"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_my_lucky)

        luckyPhraseTextView = findViewById(R.id.luckyPhraseTextView)
        luckyNumberTextView = findViewById(R.id.luckyNumberTextView)

        val userId = getUserIdFromSharedPreferences()
        val today = getCurrentDate()

        setDailyLuckyPhrase(userId, today)
        setDailyLuckyNumber(userId, today)
    }

    private fun getUserIdFromSharedPreferences(): String {
        val sharedPref = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("token", "") ?: ""
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun setDailyLuckyPhrase(userId: String, date: String) {
        val combinedString = "$userId$date"
        val phraseIndex = abs(combinedString.hashCode()) % luckyPhrases.size
        val dailyPhrase = luckyPhrases[phraseIndex]
        luckyPhraseTextView.text = dailyPhrase
    }

    private fun setDailyLuckyNumber(userId: String, date: String) {
        val combinedString = "$userId$date"
        val luckyNumber = (abs(combinedString.hashCode()) % 100) + 1 // 1부터 100까지의 숫자
        luckyNumberTextView.text = "오늘의 행운의 숫자: $luckyNumber"
    }
}