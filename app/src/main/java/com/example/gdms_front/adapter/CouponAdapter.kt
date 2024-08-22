package com.example.gdms_front.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.model.Coupon
import java.util.Locale


class CouponAdapter (private val coupons: List<Coupon>) : RecyclerView.Adapter<CouponAdapter.CouponViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_coupon, parent, false)
        return CouponViewHolder(view)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        val coupon = coupons[position]
        holder.bind(coupon)
    }

    override fun getItemCount(): Int {
        return coupons.size
    }

    class CouponViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cpnIdTextView: TextView = itemView.findViewById(R.id.cpnId)
        private val cpnProfitView: TextView = itemView.findViewById(R.id.cpnProfit)
        private val sendDateTextView: TextView = itemView.findViewById(R.id.sendDate)
        private val expDateTextView: TextView = itemView.findViewById(R.id.expDate)
        private val usedYnTextView: TextView = itemView.findViewById(R.id.usedYn)

        fun bind(coupon: Coupon) {

            when (coupon.cpnId) {
                1->{cpnIdTextView.text = "KB Wallet"
                    cpnProfitView.text = "100포인트 지급"}
                2->{cpnIdTextView.text = "CU편의점"
                    cpnProfitView.text = "3,000원 할인"}
                3->{cpnIdTextView.text = "GS25"
                    cpnProfitView.text = "3,000원 할인"}
                4->{cpnIdTextView.text = "세븐일레븐"
                    cpnProfitView.text = "3,000원 할인"}
                5->{cpnIdTextView.text = "이마트24"
                    cpnProfitView.text = "3,000원 할인"}
                6->{cpnIdTextView.text = "Happy Point"
                    cpnProfitView.text = "5,000원 할인"}
                7->{cpnIdTextView.text = "밀리의서재"
                    cpnProfitView.text = "1개월 구독권"}
                8->{cpnIdTextView.text = "쏘카"
                    cpnProfitView.text = "5,000원 할인"}
                9->{cpnIdTextView.text = "일레클"
                    cpnProfitView.text = "5,000원 할인"}
                10->{cpnIdTextView.text = "Beam"
                    cpnProfitView.text = "5,000원 할인"}
                11->{cpnIdTextView.text = "인프런"
                    cpnProfitView.text = "20% 할인"}
                12->{cpnIdTextView.text = "ELS 수수료"
                    cpnProfitView.text = "100% 할인"}
                13->{cpnIdTextView.text = "정기예금"
                    cpnProfitView.text = "0.1% 금리우대"}
                14->{cpnIdTextView.text = "정기적금"
                    cpnProfitView.text = "0.1% 금리우대"}
                15->{cpnIdTextView.text = "신용대출"
                    cpnProfitView.text = "0.1% 금리우대"}
                16->{cpnIdTextView.text = "환율"
                    cpnProfitView.text = "100%우대"}
                17->{cpnIdTextView.text = "공항철도"
                    cpnProfitView.text = "50% 할인"}
                18->{cpnIdTextView.text = "말톡(USIM/ESIM)"
                    cpnProfitView.text = "10,000원"}
            }


            // 날짜 포맷 변경
            val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

            val sendDate = inputFormat.parse(coupon.sendDate)
            val expDate = inputFormat.parse(coupon.expDate)

            sendDateTextView.text = "발급 : " +sendDate?.let { outputFormat.format(it) } ?: coupon.sendDate
            expDateTextView.text =  "만료 : " +expDate?.let { outputFormat.format(it) } ?: coupon.expDate

            usedYnTextView.text = if (coupon.usedYn) "사용완료" else "사용가능"
        }

    }
}
