package com.example.gdms_front.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.account.CategoryDetailActivity
import com.example.gdms_front.model.PayHistory
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.NumberFormat
import java.util.Locale

class CategoryAdapter(
    private val categories: List<Pair<String, Int>>,
    private val payHistoryMap: Map<CalendarDay, List<PayHistory>>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTextView: TextView = view.findViewById(R.id.categoryTextView)
        val amountTextView: TextView = view.findViewById(R.id.amountTextView)
        val arrowImageView: ImageView = view.findViewById(R.id.arrowImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val (category, amount) = categories[position]
        holder.categoryTextView.text = category

        val formattedAmount = NumberFormat.getNumberInstance(Locale.getDefault()).format(amount) + " 원"
        holder.amountTextView.text = formattedAmount

        holder.itemView.setOnClickListener {
            onItemClick(category)
        }

        holder.arrowImageView.setOnClickListener {
            val context = holder.itemView.context
            val storeCode = getStoreCode(category)
            if (storeCode != null) {
                val intent = Intent(context, CategoryDetailActivity::class.java)
                intent.putExtra("storeCode", storeCode.toString())
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "유효하지 않은 storeCode입니다.", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun getStoreCode(category: String): Any {return when (category) {
        "음식점" -> "1"
        "금융상품" -> "2"
        "면세점/해외승인" -> "3"
        "편의점" -> "4"
        "베이커리" -> "5"
        "서점" -> "6"
        "택시,주유" -> "7"
        "인강" -> "8"
        "술집" -> "9"
        "꽃집" -> "10"
        else -> "0"
    }

    }

    override fun getItemCount(): Int = categories.size

}