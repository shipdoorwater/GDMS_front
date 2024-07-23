package com.example.gdms_front.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.gdms_front.R
import com.example.gdms_front.model.ShopModel
import org.w3c.dom.Text

class RecommendedShopAdapter(private val shopList: MutableList<ShopModel>): RecyclerView.Adapter<RecommendedShopAdapter.ShopViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendedShopAdapter.ShopViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommended_shop, parent, false)

        return ShopViewHolder(view)
    }

    interface ItemClick {
        fun onClick(view: View, Position: Int)
    }
    var itemClick : ItemClick? = null

    override fun onBindViewHolder(holder: RecommendedShopAdapter.ShopViewHolder, position: Int) {

//        if (itemClick!= null) {
//            holder.itemView.setOnClickListener {
//                v -> itemClick?.onClick(v, position)
//            }
//        }

        holder.bindItems(shopList[position])
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    fun updateData(newShopList: List<ShopModel>) {
        shopList.clear()
        shopList.addAll(newShopList)
        notifyDataSetChanged()
    }

    class ShopViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItems(item : ShopModel) {
            val shopName = itemView.findViewById<TextView>(R.id.shopName)
            val shopInfo = itemView.findViewById<TextView>(R.id.shopInfo)
            val shopScore = itemView.findViewById<TextView>(R.id.shopScore)
            val shopReviewCount = itemView.findViewById<TextView>(R.id.shopReviewCount)

            shopName.text = item.shopName.toString()
            shopInfo.text = item.shopDescript.toString()
            shopScore.text = item.shopScore.toString()
            shopReviewCount.text = item.shopDescript.toString()

        }

    }

}