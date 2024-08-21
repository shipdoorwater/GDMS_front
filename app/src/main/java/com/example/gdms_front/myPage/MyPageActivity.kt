package com.example.gdms_front.myPage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.gdms_front.R
import com.example.gdms_front.auth.LoginActivity
import com.example.gdms_front.model.MemberInfoResponse
import com.example.gdms_front.model.UploadResponse
import com.example.gdms_front.network.MyPageApiService
import com.example.gdms_front.network.RetrofitClient
import com.google.android.material.imageview.ShapeableImageView
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.Date
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.util.Locale

class MyPageActivity : AppCompatActivity() {

    companion object {
        private lateinit var currentPhotoPath: String
        private const val BASE_URL = "http://211.45.162.203:8080/"
        private const val PICK_IMAGE_REQUEST = 1
    }
    private var userName: String = "사용자" // 기본값 설정

    private lateinit var myPageApiService: MyPageApiService

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var photoURI: Uri
    private lateinit var profileImageView: ShapeableImageView
    private lateinit var selectedImageFile: File
    private var currentProfileUrl: String? = null // 현재 프로필 URL을 저장할 변수

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        myPageApiService = RetrofitClient.myPageApiService

        val sharedPreference = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)

        val kakaoAppKey = getString(R.string.kakao_app_key)
        KakaoSdk.init(this, kakaoAppKey)

        if (userId != null) {
            Log.d("userIdLoading", userId)
            getMemberInfo(userId)
        }

        // 권한 부여
        requestPermissionsIfNecessary()


        // 뒤로 가기
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            finish()
        }

        // 수정화면으로 이동
        findViewById<CardView>(R.id.modifyBtn).setOnClickListener {
            val intent = Intent(this, ModifyInfoActivity::class.java)
            startActivity(intent)
        }

        // 프로필 이미지 변경화면으로 이동
        profileImageView = findViewById(R.id.profileImageView)
        findViewById<ImageView>(R.id.profileImageView).setOnClickListener {
            showProfileChangeDialog()
        }

        // 갤러리에서 이미지 선택
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    selectedImageFile = uriToFile(it)
                    updateDialogImage(it)
                }
            }
        }


        // 로그아웃
        findViewById<TextView>(R.id.logoutBtn).setOnClickListener {
            performLogout()
        }

        // 카메라로 사진 찍기
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedImageFile = File(currentPhotoPath)
                updateDialogImage(photoURI)
            }
        }
    }

    private fun getMemberInfo(userId: String) {
        myPageApiService.getMemberInfo(userId).enqueue(object : Callback<MemberInfoResponse> {
            override fun onResponse(
                call: Call<MemberInfoResponse>,
                response: Response<MemberInfoResponse>
            ) {
                if (response.isSuccessful) {
                    val memberInfo = response.body()
                    if (memberInfo != null) {
                        userName = memberInfo.userName
                        findViewById<TextView>(R.id.userName_Header).text = memberInfo.userName
                        findViewById<TextView>(R.id.userName_myPage_main).text = memberInfo.userName
                        findViewById<TextView>(R.id.userBirthday_myPage_main).text = memberInfo.birthDate
                        findViewById<TextView>(R.id.userPhone_myPage_main).text = memberInfo.userPhone
                        findViewById<TextView>(R.id.userEmail_myPage_main).text = memberInfo.userEmail
                        findViewById<TextView>(R.id.userAddress_myPage_main).text = memberInfo.userAddress
                        currentProfileUrl = memberInfo.profileUrl // 현재 프로필 URL 저장
                        loadProfileImage(memberInfo.profileUrl)
                        Log.d("MyPageAPITest", "profileUrl response 확인 :  ${memberInfo.profileUrl}")

                    } else {

                    }
                } else if (response.code() == 404) {

                } else {

                }
            }

            override fun onFailure(call: Call<MemberInfoResponse>, t: Throwable) {

            }
        })
    }

    private fun loadProfileImage(profileUrl: String?) {
        if (profileUrl != null) {
            val fullUrl = "http://211.45.162.203:8080$profileUrl" // 서버 URL을 추가하세요.
            Glide.with(this)
                .load(fullUrl)
                .placeholder(R.drawable.person_add_circle)
                .error(R.drawable.person_add_circle)
                .into(profileImageView)
        } else {
            profileImageView.setImageResource(R.drawable.person_add_circle)
        }
    }

    private fun showProfileChangeDialog() {
        // 사용자 정의 레이아웃 인플레이트
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_profile, null)

        // AlertDialog를 생성하고 사용자 정의 레이아웃 설정
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        dialog = builder.create()


        // 사용자 정의 레이아웃 뷰 초기화
        val buttonChooseGallery = dialogView.findViewById<ImageView>(R.id.galleryBtn)
        val buttonTakePhoto = dialogView.findViewById<ImageView>(R.id.cameraBtn)
        val buttonComplete = dialogView.findViewById<CardView>(R.id.changeBtn)
        val shapeableImageView = dialogView.findViewById<ShapeableImageView>(R.id.shapeableImageView)

        // userId를 설정하는 부분 추가
        val userNameTextView = dialogView.findViewById<TextView>(R.id.userName)
        userNameTextView.text = userName


        // 다이얼로그 열 때 현재 프로필 이미지 설정
        loadDialogProfileImage(shapeableImageView)

        // 갤러리 선택
        buttonChooseGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }


        // 사진찍기 선택
        buttonTakePhoto.setOnClickListener {
            takePhoto()
        }

        // 완료 버튼 클릭 시 이미지 업로드
        buttonComplete.setOnClickListener {
            uploadFile {
                // 업로드 성공 시 실행될 코드
                dialog.findViewById<ShapeableImageView>(R.id.shapeableImageView)?.drawable?.let {
                    profileImageView.setImageDrawable(it)
                }
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun loadDialogProfileImage(shapeableImageView: ShapeableImageView) {
        if (currentProfileUrl != null) {
            val fullUrl = "http://211.45.162.203:8080$currentProfileUrl"
            Glide.with(this)
                .load(fullUrl)
                .placeholder(R.drawable.person_add_circle)
                .error(R.drawable.person_add_circle)
                .into(shapeableImageView)
        } else {
            shapeableImageView.setImageResource(R.drawable.person_add_circle)
        }
    }


    private fun takePhoto() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e("MyPageActivity", "Error creating image file", ex)

                    null
                }
                photoFile?.let {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.provider",
                        photoFile
                    )
                    cameraLauncher.launch(photoURI)
                } ?: run {

                }
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            }
        } catch (e: Exception) {
            Log.e("MyPageActivity", "Error in takePhoto", e)

        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun uriToFile(uri: Uri): File {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val filePath = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return File(filePath)
    }


    // 파일 이미지 업로드시키기
    private fun uploadFile(onSuccess: (String) -> Unit) {
        if (::selectedImageFile.isInitialized) {
            val file = selectedImageFile
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val sharedPreference = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val userId = sharedPreference?.getString("token", null)
            val userIdBody = RequestBody.create("text/plain".toMediaTypeOrNull(), userId ?: "")
            val title = RequestBody.create("text/plain".toMediaTypeOrNull(), "New Profile Title")

            val call = myPageApiService.uploadProfileImage(userIdBody, title, body)
            call.enqueue(object : Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { uploadResponse ->
                            Log.d("Upload", "Success: ${uploadResponse.message}")

                            // 새로운 프로필 URL을 받아옵니다.
                            val newProfileUrl = uploadResponse.profileUrl

                            // currentProfileUrl을 업데이트합니다.
                            currentProfileUrl = newProfileUrl

                            // 성공 콜백을 호출하며 새 URL을 전달합니다.
                            onSuccess(newProfileUrl)
                        } ?: run {
                            Log.d("Upload", "Response body is null")
                        }
                    } else {
                        Log.d("Upload", "Failed: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.d("Upload", "Error: ${t.message}")
                }
            })
        } else {
            Log.d("Upload", "No file selected")
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPermissionsIfNecessary() {
        val permissionsNeeded = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 100)
        }
    }

    private fun updateDialogImage(uri: Uri) {
        dialog.findViewById<ShapeableImageView>(R.id.shapeableImageView)?.setImageURI(uri)
    }

    private fun performLogout() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isKakaoLoggedIn = sharedPreferences.getBoolean("isKakaoLoggedIn", false)

        if (isKakaoLoggedIn) {
            performKakaoLogout()
        } else {
            performGeneralLogout()
        }
    }

    private fun performKakaoLogout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("KakaoLogout", "카카오 로그아웃 실패", error)

            } else {
                Log.i("KakaoLogout", "카카오 로그아웃 성공")
            }
            // 카카오 로그아웃 후 일반 로그아웃 처리
            clearUserData()
            navigateToLoginActivity()
        }
    }

    private fun performGeneralLogout() {
        // 일반 로그아웃 처리
        clearUserData()
        navigateToLoginActivity()
    }

    private fun clearUserData() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // 자동 로그인 설정 유지를 위해 savedUserId와 savedUserPw는 삭제하지 않음
        editor.remove("token")
        editor.remove("isKakaoLoggedIn")

        // FCM 토큰 관련 데이터 삭제 (필요한 경우)
        // editor.remove("fcmToken")

        editor.apply()

        // FCM 토큰 초기화 (선택적)
        // FirebaseMessaging.getInstance().deleteToken()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()


    }
}