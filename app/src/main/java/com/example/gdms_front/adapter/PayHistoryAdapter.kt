package com.example.gdms_front.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.model.PayHistory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PayHistoryAdapter(private var payHistoryList: List<Any>, private var showDate: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_PAY_HISTORY = 1
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())

    override fun getItemViewType(position: Int): Int {
        return if (showDate && payHistoryList[position] is DateHeader) {
            TYPE_DATE_HEADER
        } else {
            TYPE_PAY_HISTORY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_DATE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false)
            DateHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pay_history, parent, false)
            PayHistoryViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DateHeaderViewHolder) {
            holder.bind(payHistoryList[position] as DateHeader)
        } else if (holder is PayHistoryViewHolder) {
            holder.bind(payHistoryList[position] as PayHistory)
        }
    }

    override fun getItemCount(): Int = payHistoryList.size

    fun updateData(newPayHistoryList: List<PayHistory>, showDate: Boolean) {
        this.showDate = showDate
        if (showDate) {
            this.payHistoryList = newPayHistoryList.groupBy { it.payDate }
                .flatMap { (date, histories) ->
                    listOf(DateHeader(date)) + histories
                }
        } else {
            this.payHistoryList = newPayHistoryList
        }
        notifyDataSetChanged()
    }

    class PayHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStoreCode: TextView = itemView.findViewById(R.id.tvStoreCode)
        private val tvStoreName: TextView = itemView.findViewById(R.id.tvStoreName)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)

        fun bind(payHistory: PayHistory) {
            tvStoreCode.text = payHistory.storeCode.toString()
            tvStoreName.text = payHistory.storeName
            tvAmount.text = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(payHistory.amount)
        }
    }

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(dateHeader: DateHeader) {
            tvDate.text = dateHeader.date
        }
    }

    class DateHeader(val date: String)
}



/*
package com.example.gdms_front.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.model.PayHistory

class PayHistoryAdapter(private var payHistoryList: List<PayHistory>) : RecyclerView.Adapter<PayHistoryAdapter.PayHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pay_history, parent, false)
        return PayHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PayHistoryViewHolder, position: Int) {
        holder.bind(payHistoryList[position])
    }

    override fun getItemCount(): Int = payHistoryList.size

    fun updateData(newPayHistoryList: List<PayHistory>) {
        payHistoryList = newPayHistoryList
        notifyDataSetChanged()
    }

    class PayHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)

        fun bind(payHistory: PayHistory) {
            tvDate.text = payHistory.payDate
            tvAmount.text = "₩${payHistory.amount}"
        }
    }
}*/
