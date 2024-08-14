package com.example.gdms_front.navigation_frag

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.gdms_front.R
import com.example.gdms_front.alarm.NotificationViewModel
import com.example.gdms_front.auth.LoginActivity
import com.example.gdms_front.news.NewsActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.example.gdms_front.account.AccountActivity
import com.example.gdms_front.point.PointMainActivity
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.gdms_front.lucky.LuckyActivity
import com.example.gdms_front.model.PushUpdateRequest
import com.example.gdms_front.model.PushUpdateResponse
import com.example.gdms_front.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//
class MainFragment : Fragment() {

    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var notificationSwitch: SwitchCompat
    private var lastNotificationState: Boolean = false

    private val factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return NotificationViewModel(requireActivity().application) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationViewModel = ViewModelProvider(this, factory).get(NotificationViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)


        notificationSwitch = view.findViewById(R.id.notificationSwitch)

        // GIF 이미지 로드
        val imageView1: ImageView = view.findViewById(R.id.imageView1_lucky)
        val imageView2: ImageView = view.findViewById(R.id.imageView2_point)
        val imageView3: ImageView = view.findViewById(R.id.imageView3_map)
        val imageView4: ImageView = view.findViewById(R.id.imageView4_pack)
        val imageView5: ImageView = view.findViewById(R.id.imageView5_news)
        val imageView6: ImageView = view.findViewById(R.id.imageView6_account)

        loadGif(imageView1, R.drawable.wired_flat_1957_maneki_cat)
        loadGif(imageView2, R.drawable.wired_flat_453_savings_pig)
        loadGif(imageView3, R.drawable.wired_flat_1360_grocery_shelf)
        loadGif(imageView4, R.drawable.wired_flat_412_gift)
        loadGif(imageView5, R.drawable.wired_flat_177_envelope_send)
        loadGif(imageView6, R.drawable.wired_flat_153_bar_chart)

        val cardView0 = view.findViewById<CardView>(R.id.cardView0)
        val cardView1 = view.findViewById<CardView>(R.id.cardView1)
        val cardView2 = view.findViewById<CardView>(R.id.cardView2)
        val cardView3 = view.findViewById<CardView>(R.id.cardView3)
        val cardView4 = view.findViewById<CardView>(R.id.cardView4)
        val cardView5 = view.findViewById<CardView>(R.id.cardView5)
        val cardView6 = view.findViewById<CardView>(R.id.cardView6)

        // 초기 애니메이션: 0, 1, 2, 3, 4번 카드뷰 슬라이드 애니메이션
        view.viewTreeObserver.addOnGlobalLayoutListener {
            animateCardViewInFromLeft(cardView0, 0L)
            animateCardViewInFromRight(cardView1, 0L)
            animateCardViewInFromRight(cardView2, 200L)
            animateCardViewInFromLeft(cardView3, 400L)
            animateCardViewInFromRight(cardView4, 600L)
        }


        val scrollView = view.findViewById<ScrollView>(R.id.scrollView)
        // 애니메이션 실행 여부를 추적하는 플래그 변수
        var cardView5Animated = false
        var cardView6Animated = false

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollY = scrollView.scrollY

            if (scrollY > 80) { // 적절한 스크롤 위치에서 애니메이션 시작
                if (!cardView5Animated) {
                    animateCardViewInFromBottom(cardView5, 0L)
                    cardView5Animated = true
                }
                if (!cardView6Animated) {
                    animateCardViewInFromBottom(cardView6, 200L) // 6번은 지연을 줘서 실행
                    cardView6Animated = true
                }
            }
        }

        // 오늘의 운세
        view.findViewById<CardView>(R.id.cardView1).setOnClickListener {
            startActivity(Intent(activity, LuckyActivity::class.java))
        }

        // 내 포인트
        view.findViewById<CardView>(R.id.cardView2).setOnClickListener {
            startActivity(Intent(activity, PointMainActivity::class.java))
        }

        // 맛집 추천
        view.findViewById<CardView>(R.id.cardView3).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_mapFragment))
        }

        // 구독혜택 안내
        view.findViewById<CardView>(R.id.cardView4).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_profitFragment))
        }

        // 뉴스레터
        view.findViewById<CardView>(R.id.cardView5).setOnClickListener {
            startActivity(Intent(activity, NewsActivity::class.java))
        }

        // 가계부
        view.findViewById<CardView>(R.id.cardView6).setOnClickListener {
            startActivity(Intent(activity, AccountActivity::class.java))
        }


        view.findViewById<ConstraintLayout>(R.id.nav_allMenu).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_allMenuFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_profit).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_profitFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_pay).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_payFragment))
            //IntentIntegrator.forSupportFragment(this@MainFragment).initiateScan()
        }

        view.findViewById<ConstraintLayout>(R.id.nav_map).setOnClickListener {
            it.findNavController().navigate((R.id.action_mainFragment_to_mapFragment))
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기 상태 설정 및 저장
        lastNotificationState = checkNotificationPermission()
        updateSwitchState()

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != checkNotificationPermission()) {
                requestNotificationPermission()
            } else {
                updateNotificationSettingInDB(isChecked)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            // 권한 설정 화면에서 돌아온 후 상태 확인 및 업데이트
            updateSwitchState()
        }
    }

    override fun onResume() {
        super.onResume()
        checkAndUpdateNotificationState()
    }

    private fun loadGif(imageView: ImageView, gifResourceId: Int) {
        Glide.with(this)
            .asGif()
            .load(gifResourceId)
            .diskCacheStrategy(DiskCacheStrategy.NONE) // 캐시 비활성화
            .skipMemoryCache(true) // 메모리 캐시 비활성화
            .into(imageView)
    }

    private fun animateCardViewInFromLeft(view: View, startDelay: Long) {
        view.translationX = -view.width.toFloat() // 왼쪽에서 시작
        val animator = ObjectAnimator.ofFloat(view, "translationX", 0f).apply {
            duration = 1000
            this.startDelay = startDelay
            interpolator = AccelerateDecelerateInterpolator()
        }
        animator.start()
    }

    private fun animateCardViewInFromRight(view: View, startDelay: Long) {
        view.translationX = view.width.toFloat() // 오른쪽에서 시작
        val animator = ObjectAnimator.ofFloat(view, "translationX", 0f).apply {
            duration = 1000
            this.startDelay = startDelay
            interpolator = AccelerateDecelerateInterpolator()
        }
        animator.start()
    }

    private fun animateCardViewInFromBottom(view: View, startDelay: Long) {
        view.translationY = view.height.toFloat() // 아래에서 시작
        view.animate()
            .translationY(0f)
            .setDuration(1000)
            .setStartDelay(startDelay)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun checkNotificationPermission(): Boolean {
        return NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
    }

    private fun requestNotificationPermission() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
        } else {
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", requireContext().packageName)
            intent.putExtra("app_uid", requireContext().applicationInfo.uid)
        }
        startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE)
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
    }

    private fun updateSwitchState() {
        val isEnabled = checkNotificationPermission()
        notificationSwitch.isChecked = isEnabled
    }

    private fun updateNotificationSettingInDB(isEnabled: Boolean) {
        val sharedPreference = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreference.getString("token", null)

        if (userId != null) {
            val request = PushUpdateRequest(userId, isEnabled)
            RetrofitClient.noticeApiService.updatePushYn(request).enqueue(object :
                Callback<PushUpdateResponse> {
                override fun onResponse(call: Call<PushUpdateResponse>, response: Response<PushUpdateResponse>) {
                    if (response.isSuccessful) {
                        Log.d("MainFragment", "Notification setting updated successfully: ${response.body()?.message}")
                    } else {
                        Log.e("MainFragment", "Failed to update notification setting: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<PushUpdateResponse>, t: Throwable) {
                    Log.e("MainFragment", "Error updating notification setting", t)
                }
            })
        } else {
            Log.e("MainFragment", "User ID is null")
        }
    }

    private fun checkAndUpdateNotificationState() {
        val currentState = checkNotificationPermission()
        updateNotificationSettingInDB(currentState)
    }
}