package com.example.gdms_front.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.model.ServicePack
import com.example.gdms_front.profit.ServicePackDetailActivity

class SubscriptionAdapter (
    private val context: Context,
    private val servicePacks: List<ServicePack>,
    //private val onItemClick: (ServicePack) -> Unit
) : RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val packName: TextView = itemView.findViewById(R.id.packName)
        val packBrief: TextView = itemView.findViewById(R.id.packBrief)  // 여기에 아이템별 표시하고 싶은 아이템 추가해
        val addButton: Button = itemView.findViewById(R.id.addButton) 
        val cardView: View = itemView as CardView // card view를 참조함
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sub_pack, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val servicePack = servicePacks[position]
        val packId = servicePack.packId
        holder.packName.text = servicePack.packName
        holder.packBrief.text = servicePack.packBrief  // 여기에 아이템별 표시하고 싶은 아이템 추가해

        when(position%3){
         //   0 -> holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCDD2"))
            0 -> holder.cardView.setBackgroundColor(Color.parseColor("#FFCDD2"))
            1 -> holder.cardView.setBackgroundColor(Color.parseColor("#C8E6C9"))
            2 -> holder.cardView.setBackgroundColor(Color.parseColor("#BBDEFB"))
        }

        holder.addButton.setOnClickListener {
            //onItemClick(servicePack)
            val intent = Intent(context, ServicePackDetailActivity::class.java)
            intent.putExtra("FRAGMENT_INDEX", position) // 인덱스 전달
            intent.putExtra("packId", packId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = servicePacks.size
}
