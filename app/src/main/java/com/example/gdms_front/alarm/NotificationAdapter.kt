package com.example.gdms_front.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.databinding.NotificationListItemBinding
import com.example.gdms_front.model.Notification

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
            binding.notificationTimestamp.text = android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", notification.timestamp)
            if (notification.isRead) {
                binding.root.setBackgroundResource(R.color.white)
            } else {
                binding.root.setBackgroundResource(R.color.purple_500)
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
}