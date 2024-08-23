package com.example.gdms_front.adapter

import android.content.Context
import android.graphics.Color
import android.icu.text.NumberFormat
import android.icu.text.SimpleDateFormat
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.model.Subscription
import java.util.Locale

class SubNowAdapter(
    private val context: Context,
    private var subscriptions: List<Subscription>,
    private val onCancelClick: (String, Int) -> Unit // packId와 userId를 전달
) : RecyclerView.Adapter<SubNowAdapter.SubscriptionViewHolder>() {

    inner class SubscriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tierTextView: TextView = itemView.findViewById(R.id.tierTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountPaid: TextView = itemView.findViewById(R.id.amountPaid)
        val cancelButton: CardView = itemView.findViewById(R.id.cancelButton)

        fun bind(subscription: Subscription) {

            if(subscription.packName=="공통"){
                tierTextView.text="2티어 서비스"
            } else {
                tierTextView.text = "3티어 ${subscription.packName}팩"
            }

            val formattedAmount = NumberFormat.getNumberInstance(Locale.getDefault()).format(subscription.amountPaid)
            amountPaid.text = "${formattedAmount} 원"


            //subStatus에 따라 취소 버튼의 가시성을 설정
            cancelButton.visibility=if(subscription.subStatus) View.VISIBLE else View.GONE

            // 구독 해지 상태에 따라 descriptionTextView의 텍스트를 설정
            val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault()) // 기존 날짜 형식이 yyyymmdd인 경우
            val outputFormat = SimpleDateFormat("yy.M.d", Locale.getDefault())

// 날짜를 포맷팅하여 문자열로 변환
            val formattedStartDate = outputFormat.format(inputFormat.parse(subscription.startDate)!!)
            val formattedEndDate = outputFormat.format(inputFormat.parse(subscription.endDate)!!)

            if (subscription.subStatus) {
                descriptionTextView.text = "${formattedStartDate} ~ ${formattedEndDate}"
            } else {
                val fullText = "${formattedStartDate} ~ ${formattedEndDate}\n해지 신청 완료"
                val spannableString = SpannableString(fullText)
                val redColor = ForegroundColorSpan(Color.RED) // 원하는 색상으로 변경 가능

                // "해지 신청 완료" 텍스트의 시작 인덱스 찾기
                val startIndex = fullText.indexOf("해지 신청 완료")

                if (startIndex != -1) {
                    spannableString.setSpan(redColor, startIndex, startIndex + "해지 신청 완료".length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                descriptionTextView.text = spannableString
            }


            //subStatus에 따라 취소 버튼의 보이나 안보이나
            cancelButton.visibility=if(subscription.subStatus) View.VISIBLE else View.GONE

            cancelButton.setOnClickListener {

                AlertDialog.Builder(context)
                    .setTitle("구독 해지")
                    .setMessage("정말로 구독을 해지하시겠습니까?")
                    .setPositiveButton("네") { dialog, which ->
                        onCancelClick(subscription.userId, subscription.packId)
                    }

                    .setNegativeButton("아니요") { dialog, which -> dialog.dismiss()
                    }

                    .show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_sub_now, parent, false)
        return SubscriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        holder.bind(subscriptions[position])
    }

    override fun getItemCount(): Int = subscriptions.size

    fun updateSubscriptions(newSubscriptions: List<Subscription>) {
        subscriptions = newSubscriptions
        notifyDataSetChanged()
    }
}