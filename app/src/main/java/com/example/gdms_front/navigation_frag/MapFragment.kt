package com.example.gdms_front.navigation_frag

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    // 사용자 위치 정보를 사용하기 위해 필요한 위치 권한을 요청하는 기능
    // arrayOf(Manifest.permission.ACCESS_FINE_LOCATION) : 요청할 권한의 목록이며,
    // ACCESS_FINE_LOCATIONS는 기기의 정확한 위치에 접근할 수 있도록 허용
    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }

    // 위치 권한 요청 코드는 정수형 상수로 정의 / 권한 요청과 관련된 결과를 식별할 수 있음
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }



    // onRequestPermissionsResult는 권한 요청의 결과를 처리하는 콜백함수
    // requestCode : 권한 요청을 식별하기 위한 코드, permission : 요청한 권한 배열,
    // grantResults : 요청에 대한 결과 배열이며 각 권한에 대한 PERMISSION_GRANTED 또는 PERMISIION_DENIED 중 하나의 값을 가짐
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한이 거부된 경우
                Log.d("locationCheck", "권한거부됨")
                naverMap?.locationTrackingMode = LocationTrackingMode.None
            } else {
                Log.d("locationCheck", "권한허용됨")
                naverMap?.locationTrackingMode = LocationTrackingMode.Follow
            }
            return
        }
        // locationSource.onRequestPermissionsResult가 true를 반환하지 않으면, 기본 구현을 호출하여 다른 권한 요청의 결과를 처리
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // api req/res 함수
    private fun callApiWithLocation(latitude: Double, longitude: Double) {
        val api = RetrofitClient.mapApiService
        api.getNearbyShops(latitude, longitude).enqueue(object : Callback<List<ShopModel>> {
            override fun onResponse(call: Call<List<ShopModel>>, response: Response<List<ShopModel>>) {
                if (response.isSuccessful) {
                    val shops = response.body()
                    if (shops != null){
                        shopAdapter.updateData(shops)
                        addMarkers(shops)
                    }
                    Log.d("MapApiService", "Shops: $shops")
                } else {
                    Log.e("MapApiService", "Response Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ShopModel>>, t: Throwable) {
                Log.e("MainActivity", "API Call Failed: ${t.message}")
            }
        })
    }


    // 카메라 이동 및 줌 확대 설정
    private fun moveCameraToLocation(latitude: Double, longitude: Double) {
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(LatLng(latitude, longitude), 15.5)
        naverMap.moveCamera(cameraUpdate)
    }


    // 받은 데이터 기준으로 맵에 마커 찍기
    private fun addMarkers(shops: List<ShopModel>) {
        shops.forEach { shop ->
            val latitude = shop.storeLatitude
            val longitude = shop.storeLongitude
            if (latitude != null && longitude != null) {
                Marker().apply {
                    position = LatLng(latitude, longitude)
                    map = naverMap
                    width = 70
                    height = 100
                    tag = shop
                    icon = OverlayImage.fromResource(R.drawable.coin_icon) //아이콘 모양
                    setOnClickListener {
                        Log.d("MarkerAPITest", "Marker clicked: ${shop.bizNo}")
                        handleMarkerClick(this)
                        true
                    }
                }
                Log.d("MarkerTest", "Marker added for shop: ${shop.shopName}")
            } else {
                Log.d("MarkerTest", "Invalid location for shop: ${shop.shopName}")
            }
        }
    }


    private fun handleMarkerClick(marker: Marker) {
        currentLocation?.let {
            val markerLocation = Location("").apply {
                latitude = marker.position.latitude
                longitude = marker.position.longitude
            }
            val distance = it.distanceTo(markerLocation)

            if (distance <= 50) {
                // 50미터 이내인 경우 모달 창을 띄우고 API 요청
                Log.d("MarkerTest", "50미터 이내")
                showModalAndRequestApi(marker.tag as ShopModel)
            } else {
                // 50미터 이상인 경우 RecyclerView의 해당 아이템으로 이동
                moveToShopItem(marker.tag as ShopModel)
            }
        }
    }

    private fun showModalAndRequestApi(shop: ShopModel) {
        val sharedPreference = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)

        // API 요청 보내기 (구현 필요)
        if (userId != null) {


            val api = RetrofitClient.mapApiService
            lifecycleScope.launch {
                try {
                    val request = GetPointRequest(userId, shop.bizNo)
                    val response = api.getPointByVisit(request)
                    if (response.isSuccessful) {

                    } else {
                        Log.d("MarkerAPITest", "실패: " + response.toString())
                        Log.e("MarkerAPITest", "Response Error: ${response.code()}, message: ${response.message()}")
                    }
                } catch (e: Exception) {
                    Log.e("MarkerAPITest", "API Call Failed: ${e.message}")
                }
            }
        } else {
            Log.d("MarkerAPITest", "UserId not found")
        }

//            val api = RetrofitClient.mapApiService
//            api.getPointByVisit(userId, shop.bizNo).enqueue(object : Callback<GetPointResponse> {
//
//
//                override fun onResponse(call: Call<GetPointResponse>, response: Response<GetPointResponse>) {
//
//                    Log.d("MarkerAPITest", "userId : $userId")
//                    Log.d("MarkerAPITest", "bizNo : ${shop.bizNo}")
//
//                    if (response.isSuccessful) {
//                        Log.d("MarkerAPITest", "성공: " + response.toString())
//                        Log.d("MarkerAPITest", "모달창 구현")
//                        // 모달 창에 데이터 표시 (구현 필요)
//                    } else {
//                        Log.d("MarkerAPITest", "실패: " + response.toString())
//                        Log.e("MarkerAPITest", "Response Error: ${response.code()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<GetPointResponse>, t: Throwable) {
//                    Log.e("MarkerAPITest", "API Call Failed: ${t.message}")
//                }
//            })
//
//        } else {
//            Log.d("MarkerAPITest", "UserId not fount")
//        }
    }

    private fun moveToShopItem(shop: ShopModel) {
        val position = shopList.indexOfFirst { it.bizNo == shop.bizNo }
        if (position != -1) {
            recyclerView.smoothScrollToPosition(position)
        }
    }

}