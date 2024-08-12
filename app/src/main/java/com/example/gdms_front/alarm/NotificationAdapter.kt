package com.example.gdms_front.alarm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.databinding.NotificationListItemBinding
import com.example.gdms_front.model.Notification
import java.util.Date

class NotificationAdapter(private var notifications: List<Notification>, private val onItemClick : (Notification) -> Unit) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: NotificationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(notifications[position])
                }
            }
        }

        fun bind(notification: Notification) {
            binding.notificationTitle.text = notification.title
            binding.notificationMessage.text = notification.content
            binding.notificationTimestamp.text = getRelativeTimeSpanString(itemView.context, notification.timestamp)
            if (notification.isRead) {
//                binding.root.setBackgroundResource(R.color.white)
            } else {
                binding.root.setBackgroundResource(R.color.mint)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NotificationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount() = notifications.size

    fun updateNotifications(notifications: List<Notification>) {
        this.notifications = notifications
        notifyDataSetChanged()
    }

    private fun getRelativeTimeSpanString(context: Context, timeInMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timeInMillis

        return when {
            diff < 1000 * 60 -> "방금 전"
            diff < 1000 * 60 * 60 -> "${diff / (1000 * 60)}분 전"
            diff < 1000 * 60 * 60 * 24 -> "${diff / (1000 * 60 * 60)}시간 전"
            diff < 1000 * 60 * 60 * 24 * 7 -> "${diff / (1000 * 60 * 60 * 24)}일 전"
            else -> {
                val date = Date(timeInMillis)
                android.text.format.DateFormat.format("yyyy-MM-dd", date).toString()
            }
        }
    }

}