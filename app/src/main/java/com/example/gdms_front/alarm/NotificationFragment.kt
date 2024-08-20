package com.example.gdms_front.alarm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gdms_front.R
import com.example.gdms_front.board.EventPageActivity
import com.example.gdms_front.board.NoticePageActivity
import com.example.gdms_front.databinding.FragmentNotificationBinding
import com.example.gdms_front.model.Notification
import com.example.gdms_front.point.PointHistoryActivity


class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding
    private val notificationViewModel: NotificationViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreference = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)

        // ViewModel에 사용자 ID 설정
        userId?.let { notificationViewModel.setUserId(it) }

        val adapter = NotificationAdapter(emptyList()) { notification ->
            handleNotificationClick(notification)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        notificationViewModel.allNotifications.observe(viewLifecycleOwner, Observer { notifications ->
            updateUI(notifications, adapter)
        })
    }

    private fun updateUI(notifications: List<Notification>, adapter: NotificationAdapter) {
        if (notifications.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
            adapter.updateNotifications(notifications)
        }
    }

    private fun handleNotificationClick(notification: Notification) {
        notificationViewModel.markAsRead(notification)

        when (notification.title) {
            "공지사항" -> navigateToNoticeDetail(notification)
            "이벤트" -> navigateToEventDetail(notification)
            "포인트 사용", "방문 포인트 적립", "결제 포인트 적립" -> navigateToPointHistory()


            else -> {
                // 기본 동작 또는 에러 처리
            }
        }
    }

    private fun navigateToNoticeDetail(notification: Notification) {
        val intent = Intent(requireContext(), NoticePageActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToEventDetail(notification: Notification) {
        val intent = Intent(requireContext(), EventPageActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToPointHistory() {
        val intent = Intent(requireContext(), PointHistoryActivity::class.java)
        startActivity(intent)
    }





}