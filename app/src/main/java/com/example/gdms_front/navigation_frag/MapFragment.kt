package com.example.gdms_front.navigation_frag

import android.Manifest
import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.example.gdms_front.R
import com.example.gdms_front.adapter.RecommendedShopAdapter
import com.example.gdms_front.model.GetPointRequest
import com.example.gdms_front.model.GetPointResponse
import com.example.gdms_front.model.ShopModel
import com.example.gdms_front.network.RetrofitClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.UiSettings
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment(), OnMapReadyCallback {

    // 위치 설정을 위해 필요한 변수들
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // RecyclerView 표시를 위해 필요한 변수들
    private lateinit var recyclerView: RecyclerView
    private lateinit var shopAdapter: RecommendedShopAdapter
    private var shopList = mutableListOf<ShopModel>()

    // 마커 클릭 이벤트를 위해 필요한 변수들
    private var marekrList = mutableListOf<Marker>()
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view  = inflater.inflate(R.layout.fragment_map, container, false)

        //RecyclerView 설정
        recyclerView = view.findViewById(R.id.recommendedShopRV)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        shopAdapter = RecommendedShopAdapter(shopList)
        recyclerView.adapter = shopAdapter

        // 네비게이션 이동
        view.findViewById<ConstraintLayout>(R.id.nav_allMenu).setOnClickListener {
            it.findNavController().navigate((R.id.action_mapFragment_to_allMenuFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_profit).setOnClickListener {
            it.findNavController().navigate((R.id.action_mapFragment_to_profitFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_pay).setOnClickListener {
            it.findNavController().navigate((R.id.action_mapFragment_to_payFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_main).setOnClickListener {
            it.findNavController().navigate((R.id.action_mapFragment_to_mainFragment))
        }

        // NaverMap Fragment 설정
        val mapFragment = childFragmentManager.findFragmentById(R.id.child_fragment_container) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.child_fragment_container, it).commit()
            }
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        // 위치 소스 설정
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // UI 설정
        val uiSettings: UiSettings = naverMap.uiSettings
        uiSettings.isLocationButtonEnabled = true

        // 위치권한 요청
        requestLocationPermission()

        // 현재 위치 가져오기 및 API 호출
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation = it
                    callApiWithLocation(it.latitude, it.longitude)
                    moveCameraToLocation(it.latitude, it.longitude)
                }
            }
        }

    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                Log.d("locationCheck", "권한거부됨")
                naverMap?.locationTrackingMode = LocationTrackingMode.None
            } else {
                Log.d("locationCheck", "권한허용됨")
                naverMap?.locationTrackingMode = LocationTrackingMode.Follow
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun callApiWithLocation(latitude: Double, longitude: Double) {
        val api = RetrofitClient.mapApiService
        api.getNearbyShops(latitude, longitude).enqueue(object : Callback<List<ShopModel>> {
            override fun onResponse(call: Call<List<ShopModel>>, response: Response<List<ShopModel>>) {
                if (response.isSuccessful) {
                    val shops = response.body()
                    if (shops != null && shops.isNotEmpty()) {
                        activity?.runOnUiThread {
                            shopAdapter.updateData(shops)
                            addMarkers(shops)
                        }
                        Log.d("MapApiService", "Shops: $shops")
                    } else {
                        Log.d("MapApiService", "No shops received or empty list")
                    }
                } else {
                    Log.e("MapApiService", "Response Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ShopModel>>, t: Throwable) {
                Log.e("MainActivity", "API Call Failed: ${t.message}")
            }
        })
    }

    private fun moveCameraToLocation(latitude: Double, longitude: Double) {
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(LatLng(latitude, longitude), 15.5)
        naverMap.moveCamera(cameraUpdate)
    }

    private fun addMarkers(shops: List<ShopModel>) {
        shops.forEach { shop ->
            val latitude = shop.storeLatitude
            val longitude = shop.storeLongitude
            if (latitude != null && longitude != null) {

                val marker = Marker().apply {
                    position = LatLng(latitude, longitude)
                    map = naverMap
                    width = 100
                    height = 100
                    icon = OverlayImage.fromResource(R.drawable.wired_lineal_290_coin)
                    tag = shop

                    // 클릭 이벤트 리스너 추가
                    setOnClickListener {
                        handleMarkerClick(this)
                        true  // 이벤트 소비를 나타내기 위해 true 반환
                    }
                }
                // 바운스 애니메이션 적용
                applyBounceAnimation(marker)
            }
        }
    }

    // 무한 여행 모드
//    private fun applyBounceAnimation(marker: Marker) {
//        val animator = ValueAnimator.ofFloat(0f, 1f)
//        animator.duration = 1000 // 1초 동안 애니메이션 실행
//        animator.repeatCount = ValueAnimator.INFINITE
//        animator.repeatMode = ValueAnimator.REVERSE
//
//        animator.addUpdateListener { animation ->
//            val value = animation.animatedValue as Float
//            val newPosition = LatLng(
//                marker.position.latitude,
//                marker.position.longitude + (value * 0.0001) // 위도를 약간 변경
//            )
//            marker.position = newPosition
//        }
//
//        animator.start()
//    }

    private fun applyBounceAnimation(marker: Marker) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 500 // 0.5초 동안 애니메이션 실행
        animator.repeatCount = ValueAnimator.INFINITE
        animator.repeatMode = ValueAnimator.REVERSE

        val originalPosition = marker.position
        val bounceDistance = 0.0001 // 조정 가능한 바운스 거리

        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            val newPosition = LatLng(
                originalPosition.latitude + (value * bounceDistance),
                originalPosition.longitude
            )
            marker.position = newPosition
        }

        animator.start()
    }

    private fun handleMarkerClick(marker: Marker) {
        currentLocation?.let {
            val markerLocation = Location("").apply {
                latitude = marker.position.latitude
                longitude = marker.position.longitude
            }
            val distance = it.distanceTo(markerLocation)
            if (distance <= 100) {
                showModalAndRequestApi(marker.tag as ShopModel)
            } else {
                moveToShopItem(marker.tag as ShopModel)
            }
        }
    }

    private fun showModalAndRequestApi(shop: ShopModel) {
        val sharedPreference = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)

        if (userId != null) {
            val api = RetrofitClient.mapApiService
            lifecycleScope.launch {
                try {
                    val request = GetPointRequest(userId, shop.bizNo)
                    val response = api.getPointByVisit(request)
                    if (response.isSuccessful) {
                        // 성공 처리
                        showSuccessModal()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        showErrorModal(errorBody ?: "이미 방문한 장소입니다")
                    }
                } catch (e: Exception) {
                    Log.e("MarkerAPITest", "API Call Failed: ${e.message}")
                }
            }
        } else {
            Log.d("MarkerAPITest", "UserId not found")
        }
    }

    private fun moveToShopItem(shop: ShopModel) {
        val position = shopList.indexOfFirst { it.bizNo == shop.bizNo }
        if (position != -1) {
            recyclerView.smoothScrollToPosition(position)
        }
    }

    private fun showSuccessModal() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.modal_point_get_success)
        // Dialog 배경을 투명하게 설정
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val gifImageView = dialog.findViewById<ImageView>(R.id.gifImageView)
        val messageTextView = dialog.findViewById<TextView>(R.id.messageTextView)
        val confirmButton = dialog.findViewById<Button>(R.id.confirmButton)

        Glide.with(this)
            .asGif()
            .load(R.drawable.wired_linea_290_coin_gif) // success_gif.gif 파일을 res/drawable 폴더에 넣어주세요
            .into(gifImageView)

        messageTextView.text = "방문 포인트가 적립되었습니다"

        confirmButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showErrorModal(errorMessage: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.modal_point_get_error)
        // Dialog 배경을 투명하게 설정
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val messageTextView = dialog.findViewById<TextView>(R.id.messageTextView)
        val confirmButton = dialog.findViewById<Button>(R.id.confirmButton)

        messageTextView.text = "오늘 이미 방문한 장소입니다"

        confirmButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
