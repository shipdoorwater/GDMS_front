package com.example.gdms_front.myPage

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.gdms_front.BuildConfig
import com.example.gdms_front.R
import com.example.gdms_front.model.MemberInfoResponse
import com.example.gdms_front.model.UploadResponse
import com.example.gdms_front.network.MyPageApiService
import com.example.gdms_front.network.RetrofitClient
import com.google.android.material.imageview.ShapeableImageView
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

class MyPageActivity : AppCompatActivity() {

    companion object {
        private lateinit var currentPhotoPath: String
        private const val BASE_URL = "http://192.168.0.73:8080/"
        private const val PICK_IMAGE_REQUEST = 1
    }

    private lateinit var myPageApiService: MyPageApiService

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var photoURI: Uri
    private lateinit var profileImageView: ShapeableImageView
    private lateinit var selectedImageFile: File
    private var currentProfileUrl: String? = null // 현재 프로필 URL을 저장할 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        myPageApiService = RetrofitClient.myPageApiService

        val sharedPreference = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userId = sharedPreference?.getString("token", null)

        if (userId != null) {
            getMemberInfo(userId)
        }

        // 뒤로 가기
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            finish()
        }

        // 수정화면으로 이동
        findViewById<TextView>(R.id.modifyBtn).setOnClickListener {
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
                    profileImageView.setImageURI(it)
                    selectedImageFile = uriToFile(it)
                }
            }
        }

        // 카메라로 사진 찍기
        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                profileImageView.setImageURI(photoURI)
                selectedImageFile = File(photoURI.path)
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
                        Toast.makeText(
                            this@MyPageActivity,
                            "회원 정보를 가져올 수 없습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (response.code() == 404) {
                    Toast.makeText(this@MyPageActivity, "사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this@MyPageActivity,
                        "회원 정보를 가져오는 중 오류가 발생했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<MemberInfoResponse>, t: Throwable) {
                Toast.makeText(
                    this@MyPageActivity,
                    "네트워크 오류가 발생했습니다: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun loadProfileImage(profileUrl: String?) {
        if (profileUrl != null) {
            val fullUrl = "http://192.168.0.73:8080$profileUrl" // 서버 URL을 추가하세요.
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
        val dialog = builder.create()


        // 사용자 정의 레이아웃 뷰 초기화
        val buttonChooseGallery = dialogView.findViewById<ImageView>(R.id.galleryBtn)
        val buttonTakePhoto = dialogView.findViewById<ImageView>(R.id.cameraBtn)
        val buttonComplete = dialogView.findViewById<Button>(R.id.changeBtn)
        val shapeableImageView = dialogView.findViewById<ShapeableImageView>(R.id.shapeableImageView)


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
            uploadFile()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadDialogProfileImage(shapeableImageView: ShapeableImageView) {
        if (currentProfileUrl != null) {
            val fullUrl = "http://192.168.0.73:8080$currentProfileUrl"
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        } else {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.let {
                photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", it)
                cameraLauncher.launch(photoURI)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(null)
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
    private fun uploadFile() {
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
                        Log.d("Upload", "Success: ${response.body()?.message}")
                        Toast.makeText(this@MyPageActivity, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("Upload", "Failed: ${response.errorBody()?.string()}")
                        Toast.makeText(this@MyPageActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.d("Upload", "Error: ${t.message}")
                    Toast.makeText(this@MyPageActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Log.d("Upload", "No file selected")
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }


}