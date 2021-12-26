package com.example.mltestapplication.new

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.Log
import android.view.View
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import kotlin.math.log

class DumbellCurls(context: Context, val poseInput: Pose, var isActive :Boolean) : View(context),Exercise {

    var isLeft : Boolean = false
    override val type: ExerciseType = ExerciseType.DumbellCurl_Type
    override var jointsToDisplay: MutableMap<String, PointF> = mutableMapOf()
    override var linesToDisplay: MutableList<Pair<PointF, PointF>> = mutableListOf()

    var redJointsMap: MutableMap<String, PointF> = mutableMapOf()
    private var elbowAngle : Float= 0f
    private var shoulderAngle : Float = 0f
    var errorText: MutableList<String> = MutableList<String>(0){""}

    //temporary parameters, these will be entered by the user in future
//    Maximum number of reps in a set
    var maxReps = 12
//    Maximum number of sets in total
    var maxSets = 3



    var exception = false
    init {
//        if(isActive)
            init()
    }
    fun init(){
        try {
        checkElbowPosition()
        if (isLeft){
            jointsToDisplay = mutableMapOf(
                "SHOULDER" to poseInput.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).position,
                "ELBOW" to poseInput.getPoseLandmark(PoseLandmark.LEFT_ELBOW).position,
                "WRIST" to poseInput.getPoseLandmark(PoseLandmark.LEFT_WRIST).position,
                "HIP" to poseInput.getPoseLandmark(PoseLandmark.LEFT_HIP).position,
            )

        }else{
            jointsToDisplay = mutableMapOf(
                "SHOULDER" to poseInput.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).position,
                "ELBOW" to poseInput.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).position,
                "WRIST" to poseInput.getPoseLandmark(PoseLandmark.RIGHT_WRIST).position,
                "HIP" to poseInput.getPoseLandmark(PoseLandmark.RIGHT_HIP).position,
            )
        }
        linesToDisplay = mutableListOf(
            Pair(jointsToDisplay["SHOULDER"],jointsToDisplay["ELBOW"]),
            Pair(jointsToDisplay["ELBOW"],jointsToDisplay["WRIST"]),
            Pair(jointsToDisplay["SHOULDER"],jointsToDisplay["HIP"]),
        ) as MutableList<Pair<PointF, PointF>>
        elbowAngle = Calculations().giveAnglesBetween(jointsToDisplay["WRIST"]!!,jointsToDisplay["ELBOW"]!!,jointsToDisplay["SHOULDER"]!!)
        shoulderAngle = Calculations().giveAnglesBetween(jointsToDisplay["ELBOW"]!!, jointsToDisplay["SHOULDER"]!!, jointsToDisplay["HIP"]!!)
    }
    catch (e:Exception){
        Log.d("Error",e.toString())
        exception = true
    }}






//Static
    companion object{
        var currentRepPosition : String  = "notInitialised"
        var repCount : Int = 0
        var setCount : Int = 0
        var lastRepPosition : String = "notInitialised"

    //two booleans used to set if last sequence was down-up , up-down
        var downUp = false
        var upDown = false
    //
    var isFinished = false
    }

    override fun drawing(canvas: Canvas?):Canvas {
        if(setCount>=maxSets){
            isActive=false
            isFinished = true
        }

//          if(!exception && isActive){
            if(!exception){
            init()
            giveReps()
            giveRepPosition()
            giveErrorText()
            giveRedJoints()
            if (canvas !== null) {
                Draw(context).drawLines(canvas, linesToDisplay)
                Draw(context).drawPoints(canvas, jointsToDisplay, redJointsMap)
                Draw(context).drawRepsAndSets(canvas, repCount, setCount)
            }
        }
        return canvas!!
    }

    override fun giveIsFinished(): Boolean {
        return isFinished
    }
//return reps count

    fun giveReps() : Int{
        if(upDown === true){
            if(lastRepPosition === "down" && currentRepPosition === "up") downUp = true
            if (downUp === true){
                ++repCount
                downUp = false
                upDown = false
            }
        }else if (lastRepPosition === "up" && currentRepPosition==="down"){
            upDown = true
        }
        lastRepPosition = currentRepPosition
        if(repCount>maxReps){
            ++setCount
            repCount=0
        }
        return repCount
    }
    
    


    private fun giveRepPosition(): String{
        if (elbowAngle in 0.0..60.0)
            currentRepPosition = "up"
        if (elbowAngle in 125.0..180.0)
            currentRepPosition = "down"
        return currentRepPosition
    }

    private fun giveErrorText(): MutableList<String>{
        errorText.clear()
        if (elbowAngle<30){
            errorText.add("Contracting Biceps Too much")
        }
        if(elbowAngle>160){
            errorText.add("Extending elbow too much")
        }
        if(shoulderAngle>10){
            errorText.add("Elbow to forward")
        }
        return errorText
    }
    private fun giveRedJoints(): MutableMap<String, PointF>{
        redJointsMap.clear()
        if (elbowAngle<30){
            redJointsMap.put("ELBOW",jointsToDisplay["ELBOW"]!!)
        }
        if(elbowAngle>135){
            redJointsMap.put("ELBOW",jointsToDisplay["ELBOW"]!!)
        }
        if(shoulderAngle>10){
            redJointsMap.put("SHOULDER",jointsToDisplay["SHOULDER"]!!)
            redJointsMap.put("ELBOW",jointsToDisplay["ELBOW"]!!)

        }
        return  redJointsMap
    }

    private fun checkElbowPosition(){
        isLeft = poseInput.getPoseLandmark(PoseLandmark.LEFT_WRIST).position.x <= poseInput.getPoseLandmark(
            PoseLandmark.LEFT_ELBOW
        ).position.x
    }

}