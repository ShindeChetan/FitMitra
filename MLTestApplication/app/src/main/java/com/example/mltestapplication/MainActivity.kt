package com.example.mltestapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.Size
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
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
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var poseDetector :  PoseDetector
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var countdown_timer: CountDownTimer
    private lateinit var start_button: Button
    private var isActive : Boolean = false
//    private lateinit var textView : TextView
    private var isFinished : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        supportActionBar?.hide()

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
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

        start_button = findViewById(R.id.id_start_button) as Button
        start_button.setOnClickListener{
            startTimer(5)
        }

        id_stop_button.setOnClickListener{
            isActive = false
            isFinished = true
            if (binding.parentLayout.childCount > 2) binding.parentLayout.removeViews(
                2,
                binding.parentLayout.childCount - 2
            )
        }
        countdown.visibility = View.INVISIBLE
        id_stop_button.visibility = View.INVISIBLE

    }
    private fun startTimer(time_in_seconds: Long) {
        binding.parentLayout.removeView(start_button)
        countdown.visibility = View.VISIBLE
        countdown_timer = object : CountDownTimer(time_in_seconds*1000, 1000) {
            override fun onFinish() {
                isActive = true
                binding.parentLayout.removeView(countdown)
                id_stop_button.visibility = View.VISIBLE
            }

            override fun onTick(timeLeft: Long) {
                countdown.text = (timeLeft/1000).toString()
            }
        }
        countdown_timer.start()


    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindPreview(cameraProvider: ProcessCameraProvider){
        val preview =  Preview.Builder().build()
//        when true, analyses, displays and updates sets and reps of exercise

        //defining the camera, the prerequisites before opening the camera
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)



        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(resources.displayMetrics.heightPixels / 2,resources.displayMetrics.widthPixels / 2 ))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()


            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), { imageProxy ->
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees

                val image = imageProxy.image
                if (image != null) {
                    val processImage = InputImage.fromMediaImage(image, rotationDegrees)
                    var element: DrawExercise
                    val result: Task<Pose> = poseDetector
                        .process(processImage)
                        .addOnSuccessListener { pose ->
                            if(isActive && !isFinished){
                                if (binding.parentLayout.childCount > 2) binding.parentLayout.removeViews(
                                    2,
                                    binding.parentLayout.childCount - 2
                                )
                                element = DrawExercise(
                                    ExerciseType.DumbellCurl_Type,
                                    context = this,
                                    pose,
                                    isActive = true
                                )
                                isFinished = element.giveIsFinished()
                                binding.parentLayout.addView(element)
                            }
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