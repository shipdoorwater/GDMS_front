package com.example.gdms_front.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.model.Subscription

class SubNowAdapter(
    private val context: Context,
    private var subscriptions: List<Subscription>,
    private val onCancelClick: (String, Int) -> Unit // packId와 userId를 전달
) : RecyclerView.Adapter<SubNowAdapter.SubscriptionViewHolder>() {

    inner class SubscriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tierTextView: TextView = itemView.findViewById(R.id.tierTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val cancelButton: Button = itemView.findViewById(R.id.cancelButton)

        fun bind(subscription: Subscription) {
            tierTextView.text = "${subscription.packName}  (${subscription.amountPaid})"

            //subStatus에 따라 취소 버튼의 가시성을 설정
            cancelButton.visibility=if(subscription.subStatus) View.VISIBLE else View.GONE

            // 구독 해지 상태에 따라 descriptionTextView의 텍스트를 설정
            if (subscription.subStatus) {
                descriptionTextView.text = "${subscription.startDate} ~ ${subscription.endDate}"
            } else {
                descriptionTextView.text = "구독 해지 신청 완료\n${subscription.startDate} ~ ${subscription.endDate}"
            }


            //subStatus에 따라 취소 버튼의 가시성을 설정
            cancelButton.visibility=if(subscription.subStatus) View.VISIBLE else View.GONE

            cancelButton.setOnClickListener {

                AlertDialog.Builder(context)
                    .setTitle("구독 해지")
                    .setMessage("정말로 구독을 해지하려고???")
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