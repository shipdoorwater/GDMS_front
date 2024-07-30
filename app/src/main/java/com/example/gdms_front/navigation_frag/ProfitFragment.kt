package com.example.gdms_front.navigation_frag

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.gdms_front.R
import com.example.gdms_front.adapter.SubNowAdapter
import com.example.gdms_front.adapter.SubscriptionAdapter
import com.example.gdms_front.model.ServicePack
import com.example.gdms_front.model.Subscription
import com.example.gdms_front.model.cancelSubRequest
import com.example.gdms_front.network.RetrofitClient
import com.example.gdms_front.profit.TierExpActivity
import com.google.android.gms.common.api.Response
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import java.util.Date
import java.util.Locale


class ProfitFragment : Fragment() {

    private var isBenefitAvailable = true

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


        //API를 통해 구독상태를 확인
        //userId?.let{checkSubscriptionStatus(it)}

//        userId?.let {
//            checkSubscriptionStatus(it) { subscriptions ->
//                adapter.updateSubscriptions(subscriptions)
//            }
//        }

        userId?.let {
            checkSubscriptionStatus(it) {subscriptions ->
                adapter.updateSubscriptions(subscriptions)
                loadAvailableSubscriptions(subscriptions)
            }
        }


        // 예제 조건
        val isBenefitAvailable = true // true면 구독 안함 / false 면 구독 중

        // 조건에 따라 뷰를 보여줍니다
        truePage?.visibility = if (isBenefitAvailable) View.VISIBLE else View.GONE
        falsePage?.visibility = if (isBenefitAvailable) View.GONE else View.VISIBLE

        // isBenefitAvailable 이 true 인 경우
        // tier1 안내 화면
        view.findViewById<LinearLayout>(R.id.tier1).setOnClickListener {
            val intent =
                Intent(this@ProfitFragment.requireContext(), TierExpActivity::class.java)
            intent.putExtra("FRAGMENT_INDEX", 0) // 1번 FRAG는 0부터 시작
            startActivity(intent)
        }

        // tier2 안내 화면
        view.findViewById<LinearLayout>(R.id.tier2).setOnClickListener {
            val intent =
                Intent(this@ProfitFragment.requireContext(), TierExpActivity::class.java)
            intent.putExtra("FRAGMENT_INDEX", 1)
            startActivity(intent)
        }

        // tier3 안내 화면
        view.findViewById<LinearLayout>(R.id.tier3).setOnClickListener {
            val intent =
                Intent(this@ProfitFragment.requireContext(), TierExpActivity::class.java)
            intent.putExtra("FRAGMENT_INDEX", 2)
            startActivity(intent)
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



}


