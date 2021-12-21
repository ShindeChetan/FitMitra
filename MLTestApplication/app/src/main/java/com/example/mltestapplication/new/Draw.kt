package com.example.mltestapplication.new

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.mlkit.vision.pose.Pose
import java.lang.Exception

class Draw(context: Context) : View(context) {
    //context: Context?, var pose : Pose, val jointsMap : Map<String, PointF>, val linesList : List<Pair<PointF?,PointF?>>, var errorText : MutableList<String>
    //, val redJointsMap : Map<String, PointF>,val sets: Int, val reps : Int

    lateinit var pointCorrectPaint : Paint
    lateinit var pointIncorrectPaint : Paint
    lateinit var textPaint: Paint
    lateinit var linePaint: Paint
    lateinit var repBoxPaint: Paint

    var xOffset = -275f
    var yOffset = 250f
    var scalex = 1.5f
    var scaley = 1.5f

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
        }catch (e: Exception){
            Log.e("DrawInit", "Problem in Init of Draw class , msg: $e")
            Toast.makeText(context,"Error in init, check logs", Toast.LENGTH_LONG)
        }
    }


    public fun drawPoints(canvas: Canvas, jointsMap: Map<String,PointF>, redJointsMap: Map<String,PointF>){
        Log.d("DPI", "xdpi = ${resources.displayMetrics.xdpi} , ydpi = ${resources.displayMetrics.ydpi}\n" +
                "x = ${jointsMap["LEFT SHOULDER"]?.x}\n" +
                "y = ${jointsMap["LEFT SHOULDER"]?.y}\n" +
                "density = ${resources.displayMetrics.density}\n"+
                "densitydpi = ${resources.displayMetrics.densityDpi}\n"+
                "scaledDensity = ${resources.displayMetrics.scaledDensity}\n"+
                "heightPixels = ${resources.displayMetrics.heightPixels}\n"+
                "widthPixels = ${resources.displayMetrics.widthPixels}\n")
        try{
            for (joint in jointsMap){
                val x = joint.value.x * scalex + xOffset
                val y = joint.value.y * scaley + yOffset
                canvas.drawCircle(x,y,10f, pointCorrectPaint)
            }
            for (joint in redJointsMap){
                val x = joint.value.x * scalex + xOffset
                val y = joint.value.y * scaley + yOffset
                canvas.drawCircle(x,y,10f, pointIncorrectPaint)
            }
        }catch (e: Exception){
            Log.e("drawPoints", "Problem in drawPoints of Draw class , msg: $e")
            Toast.makeText(context,"Error in drawPoints, check logs", Toast.LENGTH_LONG)
        }
    }
    public fun drawLines(canvas: Canvas, linesList: List<Pair<PointF,PointF>>){

        try{
            for (line in linesList) {
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

    public fun drawRepsAndSets(canvas: Canvas, reps : Int, sets: Int){
        canvas.drawRect(700f,0f,1000f,200f,repBoxPaint )
        canvas.drawText("Reps: $reps",710f,20f,textPaint)
        canvas.drawText("Sets: $sets",710f,80f,textPaint)
    }
}
