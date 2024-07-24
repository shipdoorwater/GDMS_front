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
import androidx.camera.view.PreviewView

/**
 * A simple [Fragment] subclass.
 * Use the [PayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PayFragment : Fragment() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageAnalyzer: ImageAnalysis
    private var isQrCodeDetected = false

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

        startCamera(view)

        return view
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
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
                } catch (exc: Exception) {
                    Toast.makeText(requireContext(), "카메라 실행에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }, ContextCompat.getMainExecutor(requireContext()))
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 1001)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}