package com.example.gdms_front.navigation_frag

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.CycleInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.gdms_front.R
import com.example.gdms_front.adapter.SubNowAdapter
import com.example.gdms_front.adapter.SubscriptionAdapter
import com.example.gdms_front.model.ServicePack
import com.example.gdms_front.model.Subscription
import com.example.gdms_front.model.cancelSubRequest
import com.example.gdms_front.network.RetrofitClient
import com.example.gdms_front.profit.SubActivity2
import com.example.gdms_front.profit.TierExpActivity
import com.google.android.gms.common.api.Response
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import java.util.Date
import java.util.Locale


class ProfitFragment : Fragment() {

    private var isBenefitAvailable = true

    private lateinit var recyclerViewNosub: RecyclerView
    private lateinit var subscriptionAdapter: SubscriptionAdapter
    private lateinit var fabScrollToTop: FloatingActionButton
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var toMySubpage : FloatingActionButton

    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var snapHelper: LinearSnapHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("구독프래그먼트", "onCreate: ")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("ProfitFragment", "onCreateView: ")

        val view = inflater.inflate(R.layout.fragment_profit, container, false)

        // 뷰 참조 가져오기
        val truePage: LinearLayout? = view.findViewById(R.id.truePage)
        val falsePage: LinearLayout? = view.findViewById(R.id.falsePage)

        // 아이디 가져오기
        val sharedPreference = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)

        // RecyclerView 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val adapter = SubNowAdapter(requireContext(), emptyList()) { userId, packId ->
            cancelSubscription(userId, packId)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        //구독 안하고 있을 때의 리사이클러 뷰 보여주려고 만든것
        recyclerViewNosub = view.findViewById(R.id.recyclerView_nosub)
        recyclerViewNosub.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        loadSubscriptionPacks()


        // GIF 이미지 로드
        val imageView1: ImageView = view.findViewById(R.id.heart_img)
        val imageView2: ImageView = view.findViewById(R.id.devil_wing_img)
        val imageView3: ImageView = view.findViewById(R.id.no1_book_img)
        val imageView4: ImageView = view.findViewById(R.id.no2_book_img)
        val imageView5: ImageView = view.findViewById(R.id.no3_book_img)
        val imageView6: ImageView = view.findViewById(R.id.no4_book_img)
        val imageView7: ImageView = view.findViewById(R.id.green_check_icon1)
        val imageView8: ImageView = view.findViewById(R.id.green_check_icon2)
        val imageView9: ImageView = view.findViewById(R.id.blue_point_icon)
        val imageView10: ImageView = view.findViewById(R.id.pink_free_icon)
        val smileIcon : ImageView = view.findViewById(R.id.smileIcon)

        loadGif(imageView1, R.drawable.heart_img)
        loadGif(imageView2, R.drawable.devil_wing_img)
        loadGif(imageView3, R.drawable.no1_book_img)
        loadGif(imageView4, R.drawable.no2_book_img)
        loadGif(imageView5, R.drawable.no1_book_img)
        loadGif(imageView6, R.drawable.no2_book_img)
        loadGif(imageView7, R.drawable.green_check_icon)
        loadGif(imageView8, R.drawable.green_check_icon)
        loadGif(imageView9, R.drawable.blue_point_icon)
        loadGif(imageView10, R.drawable.pink_free_icon)
        loadGif(smileIcon, R.drawable.wired_flat_261_emoji_smile)

        nestedScrollView = view.findViewById<NestedScrollView>(R.id.nestedScrollView)
        val layout1Tier = view.findViewById<LinearLayout>(R.id.layout_1tier)
        val layout2Tier = view.findViewById<LinearLayout>(R.id.layout_2tier)
        val layout3Tier = view.findViewById<LinearLayout>(R.id.layout_3tier)
        val cardView1 = view.findViewById<CardView>(R.id.cardView1)
        val cardView2 = view.findViewById<CardView>(R.id.cardView2)
        val layout1btn=view.findViewById<LinearLayout>(R.id.layout_1tier_btn)
        val layout2btn=view.findViewById<LinearLayout>(R.id.layout_2tier_btn)
        val layout3btn=view.findViewById<LinearLayout>(R.id.layout_3tier_btn)
        val cardView8 = view.findViewById<CardView>(R.id.cardView8)
        val cardView11 = view.findViewById<CardView>(R.id.cardView11)
        val cardView12 = view.findViewById<CardView>(R.id.cardView12)
        val cardView5 = view.findViewById<CardView>(R.id.cardView5)
        val btnProfitCheck = view.findViewById<ImageButton>(R.id.btnProfitCheck)

        // 클릭 리스너 설정
        layout3btn.setOnClickListener { scrollToView(nestedScrollView, layout3Tier) }
        layout2btn.setOnClickListener { scrollToView(nestedScrollView, layout2Tier) }
        layout1btn.setOnClickListener { scrollToView(nestedScrollView, layout1Tier) }

        view.viewTreeObserver.addOnGlobalLayoutListener {
            animateCardViewInFromLeft(cardView1, 0L)
            animateCardViewInFromRight(cardView2, 0L)
            animateShake(cardView11)
        }


        userId?.let {
            checkSubscriptionStatus(it) {subscriptions ->
                adapter.updateSubscriptions(subscriptions)
                loadAvailableSubscriptions(subscriptions)
            }
        }

        fabScrollToTop = view.findViewById<FloatingActionButton>(R.id.fabScrollToTop)
        setupScrollToTopButton()

        toMySubpage = view.findViewById<FloatingActionButton>(R.id.toMySubpage)
        toMySubpage()

        // 예제 조건
        val isBenefitAvailable = true // true면 구독 안함 / false 면 구독 중

        // 조건에 따라 뷰를 보여줍니다
        truePage?.visibility = if (isBenefitAvailable) View.VISIBLE else View.GONE
        falsePage?.visibility = if (isBenefitAvailable) View.GONE else View.VISIBLE

        //11번 카드뷰 클릭하면 바로 2티어 가입할 수 있도록 화면 전환
        cardView11.setOnClickListener {
            val packId = 1
            val amountPaid = 4900
            val packName = "공통"
            val packBrief = "공통 서비스"

            val intent = Intent(activity, SubActivity2::class.java).apply {
                putExtra("userId", userId)
                putExtra("packId", packId)
                putExtra("amountPaid", amountPaid)
                putExtra("packName", packName)
                putExtra("packBrief", packBrief)
            }
            startActivity(intent)
        }

        //구독하고 있는 사람이 구독서비스 관련 안내를 눌렀을 때 설정
        btnProfitCheck.setOnClickListener{
            truePage?.visibility =  View.VISIBLE
            falsePage?.visibility =  View.GONE
            toMySubpage.show()
        }





        // 네비게이션 바
        view.findViewById<ConstraintLayout>(R.id.nav_allMenu).setOnClickListener {
            it.findNavController().navigate((R.id.action_profitFragment_to_allMenuFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_pay).setOnClickListener {
            it.findNavController().navigate((R.id.action_profitFragment_to_payFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_map).setOnClickListener {
            it.findNavController().navigate((R.id.action_profitFragment_to_mapFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_main).setOnClickListener {
            it.findNavController().navigate((R.id.action_profitFragment_to_mainFragment))
        }


        return view
    }

    private fun scrollToView(scrollView: NestedScrollView, targetView: View) {
        scrollView.post {
            val targetRect = Rect()
            targetView.getGlobalVisibleRect(targetRect)
            val scrollViewRect = Rect()
            scrollView.getGlobalVisibleRect(scrollViewRect)

            val offset =  scrollViewRect.top - scrollView.paddingTop

            val scrollY = targetRect.top - scrollViewRect.top + scrollView.scrollY + offset
            scrollView.smoothScrollTo(0, scrollY)

            Log.d("티어이동", "Scrolling to ${targetView.id}, Y: $scrollY")
        }
    }

    private fun checkSubscriptionStatus(userId: String,onResult: (List<Subscription>) -> Unit) {
        val call = RetrofitClient.apiService.getCurrentSubscriptions(userId)
        call.enqueue(object : Callback<List<Subscription>> {
            override fun onResponse(call: Call<List<Subscription>>, response: retrofit2.Response<List<Subscription>>) {
                if (response.isSuccessful) {
                    val subscriptions = response.body()
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    //subscriptions의 리스트 모든 항목을 검사하여 하나라도 현재 날짜보다 미래의 endDate를 가진
                    //구독이 잇는지 확인함!!
                    isBenefitAvailable = subscriptions?.none { subscription ->
                        subscription.endDate > currentDate
                    } ?: true

                    updateUI()
                    if (subscriptions != null) {
                        onResult(subscriptions)
                    }
                } else {
                    Log.e("ProfitFragment", "API 호출 실패: ${response.code()}")
                    isBenefitAvailable = true
                    updateUI()
                }
            }

            override fun onFailure(call: Call<List<Subscription>>, t: Throwable) {
                Log.e("ProfitFragment", "API 호출 실패", t)
                isBenefitAvailable = true
                updateUI()
                onResult(emptyList())
            }
        })
    }

    private fun updateUI() {
        view?.apply {
            findViewById<LinearLayout>(R.id.truePage)?.visibility = if (isBenefitAvailable) View.VISIBLE else View.GONE
            findViewById<LinearLayout>(R.id.falsePage)?.visibility = if (isBenefitAvailable) View.GONE else View.VISIBLE
        }
    }

    private fun cancelSubscription(userId: String, packId: Int) {
        val cancelSubRequest = cancelSubRequest(userId, packId)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.cancelSubscription(cancelSubRequest)
                if (response.isSuccessful) {
                    val cancelSubResponse = response.body()
                    cancelSubResponse?.let {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        // 구독 상태를 다시 확인하여 UI 업데이트
                        checkSubscriptionStatus(userId) { subscriptions ->
                            (view?.findViewById<RecyclerView>(R.id.recyclerView)?.adapter as? SubNowAdapter)?.updateSubscriptions(subscriptions)
                        }
                    }
                } else {
                    Toast.makeText(context, "구독 해지에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ProfitFragment", "구독 해지 API 호출 실패", e)
                Toast.makeText(context, "네트워크 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadAvailableSubscriptions(currentSubscriptions: List<Subscription>) {
        // 전체 구독 가능한 아이템 가져오기
        val call = RetrofitClient.apiService.getServicePacks()
        call.enqueue(object : Callback<List<ServicePack>> {
            override fun onResponse(
                call: Call<List<ServicePack>>,
                response: retrofit2.Response<List<ServicePack>>
            ) {
                if (response.isSuccessful) {
                    val allServicePacks = response.body() ?: emptyList()
                    // 현재 구독 중인 packId를 제외
                    val availableServicePacks = allServicePacks.filter { servicePack ->
                        currentSubscriptions.none { current -> current.packId == servicePack.packId }
                    }
                    // 가로 RecyclerView에 데이터 설정
                    setupHorizontalRecyclerView(availableServicePacks)
                }
            }

            override fun onFailure(call: Call<List<ServicePack>>, t: Throwable) {
                // 실패 처리
            }
        })
    }

    private fun setupHorizontalRecyclerView(availableServicePacks: List<ServicePack>) {
        val horizontalRecyclerView: RecyclerView = view?.findViewById(R.id.availableSubscriptionsRecyclerView) ?: return
        val horizontalAdapter = SubscriptionAdapter(requireContext(), availableServicePacks)
        horizontalRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        horizontalRecyclerView.adapter = horizontalAdapter
    }

    private fun loadSubscriptionPacks() {
        // Retrofit 호출로 서비스 팩 데이터 가져오기
        val call = RetrofitClient.apiService.getServicePacks()
        call.enqueue(object : Callback<List<ServicePack>> {
            override fun onResponse(call: Call<List<ServicePack>>, response: retrofit2.Response<List<ServicePack>>) {
                if (response.isSuccessful) {
                    val servicePacks = response.body() ?: emptyList()
                    subscriptionAdapter = SubscriptionAdapter(requireContext(), servicePacks)
                    recyclerViewNosub.adapter = subscriptionAdapter
                }
            }

            override fun onFailure(call: Call<List<ServicePack>>, t: Throwable) {
                // 실패 처리
            }
        })
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

    private fun animateShake(view: View) {
        // 애니메이션 설정: 2초 동안 좌우로 5f씩 4번 흔들림
        val shakeAnimator = ObjectAnimator.ofFloat(view, "translationX", 0f, 5f, -5f, 5f, -5f, 0f)
        shakeAnimator.duration = 2000L // 2초 동안 애니메이션 실행
        shakeAnimator.interpolator = CycleInterpolator(1f) // 2번 반복

        // AnimatorSet을 사용하여 애니메이션 시작
        val animatorSet = AnimatorSet()
        animatorSet.play(shakeAnimator)
        animatorSet.start()

        // 애니메이션이 끝난 후 1초 동안 대기한 후 다시 실행
        Handler(Looper.getMainLooper()).postDelayed({
            animateShake(view)
        }, 3000L) // 2초 애니메이션 + 1초 대기
    }

    private fun setupScrollToTopButton() {
        fabScrollToTop.setOnClickListener {
            nestedScrollView.smoothScrollTo(0, 0)
        }

        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > 500) {
                fabScrollToTop.show()
            } else {
                fabScrollToTop.hide()
            }
        })
    }

    private fun toMySubpage() {
        toMySubpage.setOnClickListener {
            view?.findViewById<LinearLayout>(R.id.truePage)!!.visibility = View.GONE
            view?.findViewById<LinearLayout>(R.id.falsePage)!!.visibility = View.VISIBLE
            toMySubpage.hide()
        }
    }


}


