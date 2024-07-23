package com.example.gdms_front.navigation_frag

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gdms_front.R
import com.example.gdms_front.adapter.RecommendedShopAdapter
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
import com.naver.maps.map.util.FusedLocationSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var locationSource: FusedLocationSource
//    private var naverMap: NaverMap? = null
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var recyclerView: RecyclerView
    private lateinit var shopAdapter: RecommendedShopAdapter
    private var shopList = mutableListOf<ShopModel>()


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

        // 마커 설정
        Marker().apply {
            position = LatLng(37.4946, 127.0276056)
            map = naverMap
            width = 50
            height = 50
        }

        requestLocationPermission()
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

                // 위치 권한 허용 시 현재 위치 가져오기
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        location?.let {
                            callApiWithLocation(it.latitude, it.longitude)
                        }
                    }
                }
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
                    }

                    // 응답 데이터를 사용하여 작업 수행
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

}