package com.example.gdms_front.profit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.adapter.CvStoreImageAdapter
import android.os.Handler

class PackNo3Fragment : Fragment() {

    private lateinit var button: ImageButton
    private var userId: String? = null
    private val packId = 4
    private val amountPaid = 4900
    private val packName = "편의점"
    private val packBrief = "편의점 할인 패키지"
    private lateinit var recyclerView: RecyclerView
    private lateinit var cvStoreImageAdapter: CvStoreImageAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var scrollPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_pack_no3, container, false)
        button = view.findViewById(R.id.button)
        recyclerView = view.findViewById(R.id.recyclerView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreference = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        userId = sharedPreference?.getString("token", null)

        Log.d("아이디 들어오나 확인", userId.toString())
        Log.d("패키지 들어오나 확인", packId.toString())

        button.setOnClickListener {
            navigateToSubActivity()
        }

        val images = listOf(R.drawable.cvstore1, R.drawable.cvstore2, R.drawable.cvstore3, R.drawable.cvstore4) // 이미지 리소스 ID 목록
        cvStoreImageAdapter = CvStoreImageAdapter(requireContext(), images)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = cvStoreImageAdapter

        startAutoScroll()
    }

    private fun startAutoScroll() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                scrollPosition++
                recyclerView.smoothScrollToPosition(scrollPosition)
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun navigateToSubActivity() {
        val intent = Intent(activity, SubActivity2::class.java).apply {
            putExtra("userId", userId)
            putExtra("packId", packId)
            putExtra("amountPaid", amountPaid)
            putExtra("packName", packName)
            putExtra("packBrief", packBrief)
        }
        startActivity(intent)
    }

}