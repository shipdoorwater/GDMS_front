package com.example.gdms_front.alarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import kotlinx.coroutines.launch


class NotificationFragment : Fragment() {

    private lateinit var notificationDao: NotificationDao
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationDao = AppDatabase.getDatabase(requireContext()).notificationDao()

        lifecycleScope.launch {
            val notifications = notificationDao.getAllNotifications()
            recyclerView.adapter = NotificationAdapter(notifications)
        }
    }
}