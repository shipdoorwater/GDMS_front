package com.example.gdms_front.adapter

import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.model.PayHistory
import java.text.SimpleDateFormat
import java.util.*

class DateGroupedPayHistoryAdapter(
    private var groupedPayHistory: Map<String, List<PayHistory>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_DATE = 0
        private const val VIEW_TYPE_PAY_HISTORY = 1
    }

    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private val headerFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

    class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.dateTextView)
    }

    class PayHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val storeNameTextView: TextView = view.findViewById(R.id.storeNameTextView)
        val amountTextView: TextView = view.findViewById(R.id.amountTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_date, parent, false)
                DateViewHolder(view)
            }
            VIEW_TYPE_PAY_HISTORY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_grouped_pay_history, parent, false)
                PayHistoryViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val groupedKeys = groupedPayHistory.keys.toList()
        var currentPos = position
        for (key in groupedKeys) {
            val payHistories = groupedPayHistory[key]!!
            if (currentPos == 0) {
                (holder as DateViewHolder).dateTextView.text = headerFormat.format(dateFormat.parse(key)!!)
                return
            }
            currentPos--
            if (currentPos < payHistories.size) {
                val payHistory = payHistories[currentPos]
                (holder as PayHistoryViewHolder).storeNameTextView.text = payHistory.storeName
                holder.amountTextView.text = "${numberFormat.format(payHistory.amount)} 원"
                return
            }
            currentPos -= payHistories.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        val groupedKeys = groupedPayHistory.keys.toList()
        var currentPos = position
        for (key in groupedKeys) {
            val payHistories = groupedPayHistory[key]!!
            if (currentPos == 0) return VIEW_TYPE_DATE
            currentPos--
            if (currentPos < payHistories.size) return VIEW_TYPE_PAY_HISTORY
            currentPos -= payHistories.size
        }
        throw IllegalArgumentException("Invalid position")
    }

    override fun getItemCount(): Int {
        var count = 0
        for (payHistories in groupedPayHistory.values) {
            count += 1 + payHistories.size
        }
        return count
    }

    fun updateData(groupedPayHistory: Map<String, List<PayHistory>>) {
        this.groupedPayHistory = groupedPayHistory
        notifyDataSetChanged()
    }
}
