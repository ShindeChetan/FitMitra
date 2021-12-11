package com.example.mltestapplication.utils

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.mltestapplication.exercises.DumbellCurls
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.lang.Exception

class Draw(context: Context?, var pose : Pose) : View(context) {

    lateinit var pointCorrectPaint : Paint
    lateinit var pointIncorrectPaint : Paint
    lateinit var textPaint: Paint
    lateinit var linePaint: Paint
    lateinit var repBoxPaint: Paint
    lateinit var bodyPartsMap : MutableMap<String, PointF>
    lateinit var  linePair : MutableList<Pair<PointF?,PointF?>>
    lateinit var errorText : MutableList<String>


    var xOffset = -275f
    var yOffset = 250f
    var scalex = 1.5f
    var scaley = 1.5f
//    private var  xdpi : Float = resources.displayMetrics.xdpi / 160
//    private var  ydpi : Float = resources.displayMetrics.ydpi / 160


    lateinit var process: Process

    // TODO: 20-11-2021 : Fix Coordinate Problem
    //TODO: 20-11-2021: Make giveReps Function and DrawReps function
    // TODO: 20-11-2021 : Make Companinan object for static variables

    companion object{
        var lastRep:String? = null
        var currentRep: String? = null
        var upDown = false
        var downUp = false
        var repCount = 0
    }




    init {
        init()
    }

    val widthRatio = 1280f/480f
    val heightRatio = 720f/360f

    private fun init(){
        try {
            pointCorrectPaint = Paint()
            pointCorrectPaint.color = Color.GREEN
            pointCorrectPaint.style = Paint.Style.FILL

            pointIncorrectPaint = Paint()
            pointIncorrectPaint.color = Color.RED
            pointIncorrectPaint.style = Paint.Style.FILL

            textPaint = Paint()
            textPaint.color = Color.BLACK
            textPaint.style = Paint.Style.FILL
            textPaint.textSize =50f

            linePaint = Paint()
            linePaint.color = Color.GREEN
            linePaint.style = Paint.Style.STROKE
            linePaint.strokeWidth = 10f

            repBoxPaint = Paint()
            repBoxPaint.color = Color.GRAY
            repBoxPaint.style = Paint.Style.FILL

            process = Process(pose)

            errorText= process.dumbellCurls().errorText

//            scalex = resources.displayMetrics.widthPixels.toFloat() / 720
//            scaley = resources.displayMetrics.heightPixels.toFloat() / 1280
//            xOffset = 0f
//            yOffset = 0f

        }catch (e: Exception){
            Log.e("DrawInit", "Problem in Init of Draw class , msg: $e")
            Toast.makeText(context,"Error in init, check logs", Toast.LENGTH_LONG)
        }


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        var x = (point.position.x * scalex)+xOffset
//        var y = (point.position.y * scaley)+yOffset
//        canvas?.drawText(text, x, y, textPaint)
//        canvas?.drawPoint(x,y, pointPaint)
        try{
            if (canvas != null) {
                drawLines(canvas)
                drawPoints(canvas)
                //giveReps()
                drawReps(canvas)


            }
        }catch (e: Exception){
            Log.e("onDraw", "Problem in onDraw of Draw class , msg: $e")
            Toast.makeText(context,"Error in onDraw, check logs", Toast.LENGTH_LONG)
        }


    }

    private fun drawPoints(canvas: Canvas){
        Log.d("DPI", "xdpi = ${resources.displayMetrics.xdpi} , ydpi = ${resources.displayMetrics.ydpi}\n" +
                "x = ${bodyPartsMap["LEFT SHOULDER"]?.x}\n" +
                "y = ${bodyPartsMap["LEFT SHOULDER"]?.y}\n" +
                "density = ${resources.displayMetrics.density}\n"+
                "densitydpi = ${resources.displayMetrics.densityDpi}\n"+
                "scaledDensity = ${resources.displayMetrics.scaledDensity}\n"+
                "heightPixels = ${resources.displayMetrics.heightPixels}\n"+
                "widthPixels = ${resources.displayMetrics.widthPixels}\n")
        try{
            //drawing dumbell curls for trial

//            for (pose in bodyPartsMap) {
//                var x = (pose.value.x * scalex) + xOffset
////                var y = (pose.value.y * scaley) + yOffset
//
//                canvas.drawPoint(x, y, pointPaint)
//            }

            var dbCurls = process.dumbellCurls()
            for (joint in dbCurls.jointsToDisplay){
//                var x = (joint.value.x * scalex) + xOffset
//                var y = (joint.value.y * scaley) + yOffset
                val x = joint.value.x * scalex + xOffset
                val y = joint.value.y * scaley + yOffset


//                    canvas.drawPoint(x, y, pointIncorrectPaint)
                canvas.drawCircle(x,y,10f, pointCorrectPaint)
            }
            for (joint in dbCurls.redJoints){
//                var x = (joint.value.x * scalex) + xOffset
//                var y = (joint.value.y * scaley) + yOffset
                val x = joint.value.x * scalex + xOffset
                val y = joint.value.y * scaley + yOffset

                canvas.drawCircle(x,y,10f, pointIncorrectPaint)
            }
        }catch (e: Exception){
            Log.e("drawPoints", "Problem in drawPoints of Draw class , msg: $e")
            Toast.makeText(context,"Error in drawPoints, check logs", Toast.LENGTH_LONG)
        }
    }
    private fun drawLines(canvas: Canvas){

        try{
            for (line in process.dumbellCurls().bodypartsLinesToDisplay) {
                var point1x = (line.first?.x ?: 5000f) * scalex + xOffset
                var point1y = (line.first?.y ?: 5000f) * scaley + yOffset
                var point2x = (line.second?.x ?: 5000f) * scalex + xOffset
                var point2y = (line.second?.y ?: 5000f) * scaley + yOffset
                if(point1x === 5000f || point1y === 5000f || point2x === 5000f || point2y === 5000f){
                    return
                }
                canvas.drawLine(point1x, point1y, point2x, point2y, linePaint)
            }
        }catch (e: Exception){
            Log.e("drawLines", "Problem in drawLines of Draw class , msg: $e")
            Toast.makeText(context,"Error in drawLines, check logs", Toast.LENGTH_LONG)
        }
    }

    private fun giveReps() : Int{
        //return ++repCount

        var dbCurls = process.dumbellCurls()
        if(dbCurls.repPosition !== null){
            currentRep = dbCurls.repPosition
        }
        if(upDown === true){
            if(lastRep === "down" && currentRep === "up") downUp = true
            if (downUp === true){
                ++repCount
                downUp = false
                upDown = false
            }
        }else if (lastRep === "up" && currentRep==="down"){
            upDown = true
        }
        lastRep = currentRep
        return repCount
    }

    fun drawReps(canvas: Canvas){
        canvas.drawRect(700f,0f,1000f,200f,repBoxPaint )
        canvas.drawText(repCount.toString(),710f,20f,textPaint)
    }

}
