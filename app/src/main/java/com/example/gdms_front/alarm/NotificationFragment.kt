package com.example.gdms_front.alarm

import android.content.Context
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
import com.example.gdms_front.databinding.FragmentNotificationBinding
import com.example.gdms_front.model.Notification


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
            adapter.updateNotifications(notifications)
        })
    }

    private fun handleNotificationClick(notification: Notification) {
        notificationViewModel.markAsRead(notification)

        when (notification.title) {
            "공지사항" -> navigateToNoticeDetail(notification)
            "이벤트" -> navigateToEventDetail(notification)
            "결제" -> navigateToPaymentDetail(notification)
            else -> {
                // 기본 동작 또는 에러 처리
            }
        }
    }

    private fun navigateToNoticeDetail(notification: Notification) {
        // 공지사항 상세 화면으로 이동
        // 예: findNavController().navigate(R.id.action_notificationFragment_to_noticeDetailFragment)
        // 필요한 경우 인자를 전달할 수 있습니다.
        // val action = NotificationFragmentDirections.actionNotificationFragmentToNoticeDetailFragment(notification.boardId)
        // findNavController().navigate(action)
    }

    private fun navigateToEventDetail(notification: Notification) {
        // 이벤트 상세 화면으로 이동
        // 예: findNavController().navigate(R.id.action_notificationFragment_to_eventDetailFragment)
    }

    private fun navigateToPaymentDetail(notification: Notification) {
        // 결제 상세 화면으로 이동
        // 예: findNavController().navigate(R.id.action_notificationFragment_to_paymentDetailFragment)
    }





}