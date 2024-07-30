package com.example.gdms_front.profit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.gdms_front.R

class PackNo7Fragment : Fragment() {

    private lateinit var button: Button
    private var userId: String? = null
    private val packId = 8
    private val amountPaid = 9900
    private val packName = "자기계발"
    private val packBrief = "자기계발 서적 패키지"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_pack_no7, container, false)
        button = view.findViewById(R.id.button)

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