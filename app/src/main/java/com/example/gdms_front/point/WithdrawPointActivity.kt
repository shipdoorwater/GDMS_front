package com.example.gdms_front.point

import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import com.example.gdms_front.R
import com.example.gdms_front.model.MyPointResponse
import com.example.gdms_front.model.WithdrawRequest
import com.example.gdms_front.model.WithdrawResponse
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WithdrawPointActivity : AppCompatActivity() {

    private lateinit var enteredAmount: TextView
    private var amount: String = ""

    private lateinit var myPointText: TextView
    private var totalPoints: Int = 0 // 포인트를 저장할 변수

    // 계좌번호 처리를 위한 변수
    private lateinit var accountInputLayout: LinearLayout
    private lateinit var accountNumberInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdraw_point)


        // 내 포인트 조회
        myPointText = findViewById(R.id.myPointText)

        val sharedPreference = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)

        if (userId != null) {
            getMyPoint(userId)
        }

        // 입력값 금액으로 출력
        enteredAmount = findViewById(R.id.enteredAmount)

        val buttonClickListener = View.OnClickListener { view ->
            val button = view as Button
            when (button.id) {
                R.id.btnCancel -> amount = ""
                R.id.btnBackSpace -> if (amount.isNotEmpty())
                    amount = amount.dropLast(1)
                else -> {
                    val newAmount = amount + button.text
                    if (newAmount.toLong() <= totalPoints) {
                        amount = newAmount
                    } else {
                        amount = totalPoints.toString()
                        Toast.makeText(this, "입력 금액이 보유 포인트를 초과할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            updateAmountDisplay()
        }

        findViewById<Button>(R.id.btn1).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.btn2).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.btn3).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.btn4).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.btn5).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.btn6).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.btn7).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.btn8).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.btn9).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.btn0).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.btnCancel).setOnClickListener(buttonClickListener)
        findViewById<Button>(R.id.btnBackSpace).setOnClickListener(buttonClickListener)


        // 계좌번호 입력
        accountInputLayout = findViewById(R.id.accountInputLayout)
        accountNumberInput = findViewById(R.id.accountNumberInput)

        findViewById<Button>(R.id.button).setOnClickListener {
            showAccountInputLayout()
        }

        findViewById<Button>(R.id.confirmButton).setOnClickListener {
            showConfirmationDialog()
        }


        // 뒤로 가기
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            finish()
        }
    }

    private fun updateAmountDisplay() {
        //  포맷팅된 문자열로 변환
        val formattedAmount = formatAmount(amount) + "원"
        enteredAmount.text = formattedAmount
    }

    private fun formatAmount(amount: String): String {
        return if (amount.isEmpty()) {
            ""
        } else {
            // 정수로 변환 후 포맷 적용
            val parsedAmount = amount.toLong()
            val formatter = DecimalFormat("#,###")
            formatter.format(parsedAmount)
        }
    }

    private fun getMyPoint(userId: String) {
        RetrofitClient.payApiService.getMyPoint(userId).enqueue(object : Callback<MyPointResponse> {
            override fun onResponse(
                call: Call<MyPointResponse>,
                response: Response<MyPointResponse>
            ) {
                if (response.isSuccessful) {
                    totalPoints = response.body()?.totalPoints ?: 0
                    myPointText.text =formatNumberWithComma(totalPoints)
                } else {
                    Log.d("PointTest", "실패")
                }
            }

            override fun onFailure(call: Call<MyPointResponse>, t: Throwable) {
                Log.d("PointTest", "에러: ${t.message}")
            }
        })
    }

    private fun formatNumberWithComma(number: Int): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(number)
    }


    private fun showAccountInputLayout() {
        // 금액 입력 레이아웃을 숨기고 계좌번호 입력 레이아웃을 표시
        findViewById<LinearLayout>(R.id.linearLayout10).visibility = View.GONE
        findViewById<GridLayout>(R.id.gridLayout).visibility = View.GONE
        enteredAmount.visibility = View.GONE
        accountInputLayout.visibility = View.VISIBLE
    }

    private fun showConfirmationDialog() {
        val accountNumber = accountNumberInput.text.toString()
        val withdrawAmount = amount.toInt()
        val remainingPoints = totalPoints - withdrawAmount

        val sharedPreference = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference.getString("token", null)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("확인")
        builder.setMessage("계좌번호: $accountNumber\n출금 금액: ${formatNumberWithComma(withdrawAmount)}원\n출금 후 잔액: ${formatNumberWithComma(remainingPoints)}원\n출금하시겠습니까?")
        builder.setPositiveButton("예") { _, _ ->
            // 확인 버튼 클릭 시 동작
            if (userId != null) {
                performWithdraw(userId, accountNumber, withdrawAmount)
            }
        }
        builder.setNegativeButton("아니오") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


    private fun performWithdraw(userId: String, accountNumber: String, amount: Int) {
        val request = WithdrawRequest(userId, amount, accountNumber)
        RetrofitClient.payApiService.withdrawPoints(request).enqueue(object : Callback<WithdrawResponse> {
            override fun onResponse(call: Call<WithdrawResponse>, response: Response<WithdrawResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@WithdrawPointActivity, "출금이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    // 출금 후 PointMainActivity로 이동
                    val intent = Intent(this@WithdrawPointActivity, PointMainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@WithdrawPointActivity, "출금에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                Toast.makeText(this@WithdrawPointActivity, "출금 요청 중 에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
