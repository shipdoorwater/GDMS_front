package com.example.gdms_front.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.model.ServicePack
import com.example.gdms_front.profit.ServicePackDetailActivity

class SubscriptionAdapter (
    private val context: Context,
    private val servicePacks: List<ServicePack>,
) : RecyclerView.Adapter<SubscriptionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val backgroundImage: ImageView = itemView.findViewById(R.id.backgroundImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sub_pack, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val servicePack = servicePacks[position]
        // 배경 이미지 설정
        val backgroundResId = when (servicePack.packId) {
            in 1..10 -> context.resources.getIdentifier("pack_card_${servicePack.packId - 1}", "drawable", context.packageName)
            else -> R.drawable.img_gallery
        }
        holder.backgroundImage.setImageResource(backgroundResId)


        holder.backgroundImage.setOnClickListener {
            val intent = Intent(context, ServicePackDetailActivity::class.java).apply {
                putExtra("FRAGMENT_INDEX", position)
                putExtra("packId", servicePack.packId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = servicePacks.size
}
