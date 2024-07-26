package com.example.gdms_front.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
        val addButton: Button = itemView.findViewById(R.id.addButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sub_pack, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val servicePack = servicePacks[position]
        holder.packName.text = servicePack.packName
        holder.addButton.setOnClickListener {
            //onItemClick(servicePack)
            val intent = Intent(context, ServicePackDetailActivity::class.java)
            intent.putExtra("FRAGMENT_INDEX", position) // 인덱스 전달
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = servicePacks.size
}
