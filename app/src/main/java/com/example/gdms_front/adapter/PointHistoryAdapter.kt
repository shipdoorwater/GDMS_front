package com.example.gdms_front.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.model.PointDetailResponse
import java.text.NumberFormat
import java.util.Locale

class PointHistoryAdapter(private var pointDetails: List<PointDetailResponse>):
    RecyclerView.Adapter<PointHistoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pointDate: TextView = itemView.findViewById(R.id.pointDate)
        val pointCategory: TextView = itemView.findViewById(R.id.pointCategory)
        val pointValue: TextView = itemView.findViewById(R.id.pointValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_point_detail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pointDetails.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pointDetail = pointDetails[position]
        holder.pointDate.text = pointDetail.pointDate
        holder.pointCategory.text = getCategoryText(pointDetail.pointCategory)

        val pointValue = if (pointDetail.pointCategory == 3) -pointDetail.pointValue else pointDetail.pointValue
        holder.pointValue.text = formatNumber(pointValue)
    }

    private fun getCategoryText(category: Int): String {
        return when (category) {
            1 -> "추천 플레이스 방문"
            2 -> "캐시백"
            3 -> "사용"
            else -> "알 수 없음"
        }
    }

    private fun formatNumber(number: Int): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
        return numberFormat.format(number)
    }

    fun updateData(newPointDetails: List<PointDetailResponse>) {
        pointDetails = newPointDetails
        notifyDataSetChanged()
    }


}