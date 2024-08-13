package com.example.gdms_front.point

import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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
        findViewById<Button>(R.id.button).visibility = View.GONE
        enteredAmount.visibility = View.GONE
        accountInputLayout.visibility = View.VISIBLE

        // accountNumberInput에 포커스 설정
        accountNumberInput.requestFocus()
        // 키보드 표시
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(accountNumberInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun showConfirmationDialog() {
        val accountNumber = accountNumberInput.text.toString()
        val withdrawAmount = amount.toInt()
        val remainingPoints = totalPoints - withdrawAmount

        // 커스텀 레이아웃 인플레이트
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_withdraw_confirm, null)

        // 뷰 참조
        val tvAccountNumber = dialogView.findViewById<TextView>(R.id.tvAccountNumber)
        val tvWithdrawAmount = dialogView.findViewById<TextView>(R.id.tvWithdrawAmount)
        val tvRemainingPoints = dialogView.findViewById<TextView>(R.id.tvRemainingPoints)
        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)

        // 데이터 설정
        tvAccountNumber.text = "$accountNumber"
        tvWithdrawAmount.text = "${formatNumberWithComma(withdrawAmount)}원"
        tvRemainingPoints.text = "${formatNumberWithComma(remainingPoints)}원"

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        // 커스텀 타이틀 설정
        val titleView = layoutInflater.inflate(R.layout.dialog_withdraw_confirm_title, null)
        builder.setCustomTitle(titleView)

        val dialog = builder.create()

        // 버튼 클릭 리스너 설정
        btnYes.setOnClickListener {
            val userId = getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getString("token", null)
            if (userId != null) {
                performWithdraw(userId, accountNumber, withdrawAmount)
            }
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        // 대화상자 스타일 설정
        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
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
