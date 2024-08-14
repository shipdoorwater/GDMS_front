package com.example.gdms_front.navigation_frag

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.gdms_front.R
import com.example.gdms_front.qr_pay.QrPayActivity
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.integration.android.IntentIntegrator
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.camera.core.Camera
import androidx.camera.view.PreviewView
import com.bumptech.glide.Glide

class PayFragment : Fragment() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageAnalyzer: ImageAnalysis
    private var isQrCodeDetected = false
    //배경 뿌옇게 보이기 추가

    private var camera: Camera? = null // 카메라 객체 추가
    private var isFlashOn = false // 플래시 상태 변수 추가


    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pay, container, false)
        //배경 뿌옇게 보이기 추가

        view.findViewById<ConstraintLayout>(R.id.nav_allMenu).setOnClickListener {
            it.findNavController().navigate((R.id.action_payFragment_to_allMenuFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_profit).setOnClickListener {
            it.findNavController().navigate((R.id.action_payFragment_to_profitFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_map).setOnClickListener {
            it.findNavController().navigate((R.id.action_payFragment_to_mapFragment))
        }

        view.findViewById<ConstraintLayout>(R.id.nav_main).setOnClickListener {
            it.findNavController().navigate((R.id.action_payFragment_to_mainFragment))
        }

        val flashButton = view.findViewById<Button>(R.id.flashButton)
        flashButton.setOnClickListener {
            toggleFlash()
        }

        startCamera(view)

        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startCamera(requireView())
                } else {
                    Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            startCamera(view)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera(view: View) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener({
                cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(view.findViewById<PreviewView>(R.id.previewView).surfaceProvider)
                    }



                val barcodeScanner = BarcodeScanning.getClient()
                imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            if (!isQrCodeDetected) {
                                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                                val inputImage = InputImage.fromMediaImage(imageProxy.image!!, rotationDegrees)
                                barcodeScanner.process(inputImage)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            val rawValue = barcode.rawValue
                                            if (rawValue != null) {
                                                isQrCodeDetected = true
                                                cameraProvider.unbindAll()
                                                val intent = Intent(requireContext(), QrPayActivity::class.java).apply {
                                                    putExtra("QR_CODE_VALUE", rawValue)
                                                }
                                                startActivity(intent)
                                                imageProxy.close()
                                                return@addOnSuccessListener
                                            }
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(requireContext(), "QR 코드 인식에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalyzer
                    )
                } catch (exc: Exception) {
                    Log.e("CameraX", "카메라 바인딩 실패", exc)
                    Toast.makeText(requireContext(), "카메라 실행에 실패했습니다: ${exc.message}", Toast.LENGTH_SHORT).show()
                }
            }, ContextCompat.getMainExecutor(requireContext()))
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 1001)
        }
    }

    private fun isFlashAvailable(): Boolean {
        return camera?.cameraInfo?.hasFlashUnit() ?: false
    }

    // 플래시 토글 함수 추가
    private fun toggleFlash() {
        Log.d("플래시", "toggleFlash called. Current state: $isFlashOn")
        if (isFlashAvailable()) {
            camera?.let {
                val cameraControl = it.cameraControl
                isFlashOn = !isFlashOn
                Log.d("플래시", "Enabling torch: $isFlashOn")
                cameraControl.enableTorch(isFlashOn)
                view?.findViewById<Button>(R.id.flashButton)?.text = if (isFlashOn) "플래시 끄기" else "플래시 켜기"
            } ?: Log.e("플래시", "Camera object is null")
        } else {
            Log.w("플래시", "Flash is not available on this device")
            Toast.makeText(requireContext(), "이 기기는 플래시를 지원하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}