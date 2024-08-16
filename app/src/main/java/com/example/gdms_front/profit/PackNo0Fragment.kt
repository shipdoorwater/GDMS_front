package com.example.gdms_front.profit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gdms_front.R

class PackNo0Fragment : Fragment() {

    private lateinit var button: CardView
    private var userId: String? = null
    private val packId = 1
    private val amountPaid = 4900
    private val packName = "공통"
    private val packBrief = "공통 서비스"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_pack_no0, container, false)
        button = view.findViewById(R.id.button)

        val imageView7: ImageView = view.findViewById(R.id.green_check_icon1)
        val imageView8: ImageView = view.findViewById(R.id.green_check_icon2)
        val imageView9: ImageView = view.findViewById(R.id.blue_point_icon)
        val imageView10: ImageView = view.findViewById(R.id.pink_free_icon)

        loadGif(imageView7, R.drawable.green_check_icon)
        loadGif(imageView8, R.drawable.green_check_icon)
        loadGif(imageView9, R.drawable.blue_point_icon)
        loadGif(imageView10, R.drawable.pink_free_icon)




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

    private fun loadGif(imageView: ImageView, gifResourceId: Int) {
        Glide.with(this)
            .asGif()
            .load(gifResourceId)
            .diskCacheStrategy(DiskCacheStrategy.NONE) // 캐시 비활성화
            .skipMemoryCache(true) // 메모리 캐시 비활성화
            .into(imageView)
    }


}
