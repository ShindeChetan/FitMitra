package com.example.mltestapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.util.Log
import android.util.Size
import androidx.core.content.ContextCompat
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import com.example.mltestapplication.databinding.ActivityMainBinding
import com.example.mltestapplication.new.DrawExercise
import com.example.mltestapplication.new.ExerciseType
import com.google.android.gms.tasks.Task
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var poseDetector :  PoseDetector
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        //getSupportActionBar()?.hide()

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            Log.d("CALLED", "Called inside cameraProviderFuture.addListener")
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider = cameraProvider)
        }, ContextCompat.getMainExecutor(this))

//        val localModel = LocalModel.Builder()
//            .setAbsoluteFilePath("pose_detection.tflite")
//            .build()

        val poseDetectorOptions =  PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()



        poseDetector = PoseDetection.getClient(poseDetectorOptions)

    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider){
        Log.d("CALLED", "Called Just inside Bind preview")
        val preview =  Preview.Builder().build()


        //defining the camera, the prerequisites before opening the camera
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)



        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(1280,720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this),{imageProxy ->
            Log.d("CALLED", "Called inside imageAnalysis.setAnalyzer")
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees

            val image = imageProxy.image

            if(image != null){
                val processImage = InputImage.fromMediaImage(image, rotationDegrees)
                var element : DrawExercise
                val result : Task<Pose> = poseDetector
                        .process(processImage)
                    .addOnSuccessListener { pose ->
//                        val allPoseLandmarks = pose.allPoseLandmarks
                        Log.d("CALLED", "Called inside poseDetector.onSuccessListener")
                        if (binding.parentLayout.childCount > 1) binding.parentLayout.removeViews( 1,
                            binding.parentLayout.childCount - 1)
                            element =DrawExercise(ExerciseType.DumbellCurl_Type,context=this, pose)
                            binding.parentLayout.addView(element)
                        imageProxy.close()
                    }.addOnFailureListener {
                        Log.v("MainActivity", "Error - ${it.message}")
                        imageProxy.close()
                    }
            }
        })

        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, imageAnalysis, preview)
    }
}