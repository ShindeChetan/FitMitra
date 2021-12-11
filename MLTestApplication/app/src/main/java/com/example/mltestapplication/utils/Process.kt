package com.example.mltestapplication.utils

import android.graphics.PointF
import android.util.Log
import com.example.mltestapplication.exercises.DumbellCurls
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.lang.Exception
import kotlin.math.atan2

class Process(var pose: Pose) {

    val DUMBELL_CURLS = "DUMBELL CURLS"
    lateinit var allJointsMap : MutableMap<String, PointF>
    lateinit var allLinePairs : MutableList<Pair<PointF?,PointF?>>


    init {
        init()
    }

    private fun init(){
        allJointsMap = mutableMapOf(
            //"NOSE" to pose.getPoseLandmark(PoseLandmark.NOSE).position,
            "LEFT SHOULDER" to pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER).position,
            "RIGHT SHOULDER" to pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER).position,
            "RIGHT ELBOW" to pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW).position,
            "LEFT ELBOW" to pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW).position,
            "LEFT WRIST" to pose.getPoseLandmark(PoseLandmark.LEFT_WRIST).position,
            "RIGHT WRIST" to pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST).position,
            "LEFT HIP" to pose.getPoseLandmark(PoseLandmark.LEFT_HIP).position,
            "RIGHT HIP" to pose.getPoseLandmark(PoseLandmark.RIGHT_HIP).position,
            "LEFT KNEE" to pose.getPoseLandmark(PoseLandmark.LEFT_KNEE).position,
            "RIGHT KNEE" to pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE).position,
            "LEFT ANKLE" to pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE).position,
            "RIGHT ANKLE" to pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE).position,
//                "LEFT FOOT INDEX" to pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX).position,
//                "RIGHT FOOT INDEX" to pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX).position,
        )

        allLinePairs = mutableListOf(
            Pair(allJointsMap["LEFT SHOULDER"],allJointsMap["RIGHT SHOULDER"]),
            Pair(allJointsMap["LEFT SHOULDER"],allJointsMap["LEFT ELBOW"]),
            Pair(allJointsMap["LEFT ELBOW"],allJointsMap["LEFT WRIST"]),
            Pair(allJointsMap["RIGHT SHOULDER"],allJointsMap["RIGHT ELBOW"]),
            Pair(allJointsMap["RIGHT ELBOW"],allJointsMap["RIGHT WRIST"]),
            Pair(allJointsMap["LEFT SHOULDER"],allJointsMap["LEFT HIP"]),
            Pair(allJointsMap["RIGHT SHOULDER"],allJointsMap["RIGHT HIP"]),
            Pair(allJointsMap["LEFT HIP"],allJointsMap["RIGHT HIP"]),
            Pair(allJointsMap["LEFT HIP"],allJointsMap["LEFT KNEE"]),
            Pair(allJointsMap["RIGHT HIP"],allJointsMap["RIGHT KNEE"]),
            Pair(allJointsMap["LEFT KNEE"],allJointsMap["LEFT ANKLE"]),
            Pair(allJointsMap["RIGHT KNEE"],allJointsMap["RIGHT ANKLE"]),
//                Pair(allJointsMap["LEFT ANKLE"],allJointsMap["LEFT FOOT INDEX"]),
//                Pair(allJointsMap["RIGHT ANKE"],allJointsMap["RIGHT FOOT INDEX"]),
        )
    }

    fun dumbellCurls() : DumbellCurls {
        //This will return
        //Joints to display, bodypart lines to be displayed, red joints to be displayed, position up/down,error text array

        //List of angles between i) Elbow joint, ii) Shoulder Joint
        var anglesList : List<Float> = dumbellCurlsJointAngles()
        //List of all joints to display for this exercise
        var jointsToDisplay = mutableMapOf<String, PointF>(
        )
        var bodypartsLinesToDisplay  : MutableList<Pair<PointF, PointF>> = mutableListOf()

        //Elbow and shoulder positions
        var elbow : Pair<String, PointF>
        var shoulder : Pair<String, PointF>

        if(anglesList[2] == 0f){
            jointsToDisplay["LEFT SHOULDER"] = allJointsMap["LEFT SHOULDER"]!!
            jointsToDisplay["LEFT ELBOW"] = allJointsMap["LEFT ELBOW"]!!
            jointsToDisplay["LEFT HIP"] = allJointsMap["LEFT HIP"]!!

            bodypartsLinesToDisplay.add(Pair(allJointsMap["LEFT SHOULDER"]!!, allJointsMap["LEFT HIP"]!!))
            bodypartsLinesToDisplay.add(Pair(allJointsMap["LEFT SHOULDER"]!!, allJointsMap["LEFT ELBOW"]!!))
            bodypartsLinesToDisplay.add(Pair(allJointsMap["LEFT ELBOW"]!!, allJointsMap["LEFT WRIST"]!!))

            elbow = Pair("LEFT ELBOW", allJointsMap["LEFT ELBOW"]!!)
            shoulder =  Pair("LEFT SHOULDER",allJointsMap["LEFT SHOULDER"]!!)

        }
        else{
            jointsToDisplay["RIGHT SHOULDER"] = allJointsMap["RIGHT SHOULDER"]!!
            jointsToDisplay["RIGHT ELBOW"] = allJointsMap["RIGHT ELBOW"]!!
            jointsToDisplay["RIGHT HIP"] = allJointsMap["RIGHT HIP"]!!

            bodypartsLinesToDisplay.add(Pair(allJointsMap["RIGHT SHOULDER"]!!, allJointsMap["RIGHT HIP"]!!))
            bodypartsLinesToDisplay.add(Pair(allJointsMap["RIGHT SHOULDER"]!!, allJointsMap["RIGHT ELBOW"]!!))
            bodypartsLinesToDisplay.add(Pair(allJointsMap["RIGHT ELBOW"]!!, allJointsMap["RIGHT WRIST"]!!))

            elbow = Pair("RIGHT ELBOW",allJointsMap["RIGHT ELBOW"]!!)
            shoulder = Pair("RIGHT SHOULDER", allJointsMap["RIGHT SHOULDER"]!!)
        }

        var redJoints :  MutableMap<String, PointF> = mutableMapOf()
        var repPosition : String? = null
        var errorText : MutableList<String> = mutableListOf()
        if(anglesList[0] in 30.0..135.0){
            repPosition = if (anglesList[0] in 30.0..60.0){
                "up"
            } else if (anglesList[0] in 125.0..150.0){
                "down"
            } else {

                null
            }
        }
        else {
            if (anglesList[0] < 30f) {

                errorText.add("Contracting Biceps too much")
            }
            if (anglesList[0] > 150f) {
                errorText.add("Extending biceps too much")
            }
            redJoints[elbow.first] = elbow.second
        }
        if(anglesList[1] > 30f){
            redJoints[shoulder.first] = shoulder.second
            errorText.add("Too much angle between shoulders")
        }
        Log.d("Angles" , "\n\n Elbow Angle : ${anglesList[0]} \n Shoulder Angle: ${anglesList[1]} ")
        return DumbellCurls(jointsToDisplay, bodypartsLinesToDisplay, redJoints,repPosition ,errorText)
    }

    private fun dumbellCurlsJointAngles(): List<Float> {

           var angleElbow : Float
           var angleShoulder : Float
           var leftRightSide : Float
           if(allJointsMap["LEFT WRIST"]?.x!! < allJointsMap["LEFT ELBOW"]?.x!!){
               angleElbow = angleBetweenThreePoints(allJointsMap["LEFT SHOULDER"]!!, allJointsMap["LEFT ELBOW"]!!, allJointsMap["LEFT WRIST"]!!)
               angleShoulder = angleBetweenThreePoints(allJointsMap["LEFT HIP"]!!, allJointsMap["LEFT SHOULDER"]!!, allJointsMap["LEFT ELBOW"]!!)
               leftRightSide = 0f
           }else{
               angleElbow = angleBetweenThreePoints(allJointsMap["RIGHT SHOULDER"]!!, allJointsMap["RIGHT ELBOW"]!!, allJointsMap["RIGHT WRIST"]!!)
               angleShoulder = angleBetweenThreePoints(allJointsMap["RIGHT HIP"]!!, allJointsMap["RIGHT SHOULDER"]!!, allJointsMap["RIGHT ELBOW"]!!)
               leftRightSide = 1f
           }
           return listOf(angleElbow, angleShoulder ,leftRightSide)
    }

//    private fun angleBetweenTwoLines(A: PointF?, B: PointF?, C: PointF?): Float{
//        var x1 = (A!!.x * scalex) + xOffset
//        var x2 = (B!!.x * scalex) + xOffset
//        var x3 = (C!!.x * scalex) + xOffset
//        var y1 = (A!!.y * scaley) + yOffset
//        var y2 = (B!!.y * scaley) + yOffset
//        var y3 = (C!!.y * scaley) + yOffset
//
//
//
//        var angle1 : Float = Math.atan2((B!!.y-A!!.y).toDouble(), (A!!.x -B!!.x).toDouble()).toFloat()
//        var angle2 : Float = Math.atan2((C!!.y-B!!.y).toDouble(), (B!!.x -C!!.x).toDouble()).toFloat()
//        var calculatedAngle: Float = Math.toDegrees((angle1 - angle2).toDouble()).toFloat()
//        if (calculatedAngle < 0) calculatedAngle += 360
//        if (calculatedAngle > 180) calculatedAngle -= 180
//
//        return calculatedAngle
//    }

    private fun angleBetweenThreePoints(a: PointF, b: PointF, c:PointF): Float {
//        a.x = (a.x * scalex) + xOffset
//        b.x = (b.x * scalex) + xOffset
//        c.x = (c.x * scalex) + xOffset
//
//        a.y = (a.y * scaley) + yOffset
//        b.y = (b.y * scaley) + yOffset
//        c.y = (c.y * scaley) + yOffset




        var ab:Double = Math.sqrt(Math.pow((a.x - b.x).toDouble(), 2.0) + Math.pow((a.y - b.y).toDouble(), 2.0))
        var ac:Double = Math.sqrt(Math.pow((a.x - c.x).toDouble(), 2.0) + Math.pow((a.y - c.y).toDouble(), 2.0))
        var bc:Double = Math.sqrt(Math.pow((b.x - c.x).toDouble(), 2.0) + Math.pow((b.y - c.y).toDouble(), 2.0))

        var cosValue = (ab * ab + bc * bc - ac * ac) /( 2 * bc * ab)
        val angle = Math.acos(cosValue) *(180/Math.PI)

//        //Optional logging to help test and debug
//        Log.d(TAG,"ab: " + ab)
//        Log.d(TAG,"ac: " + ac)
//        Log.d(TAG,"bc: " + bc)
//        Log.d(TAG,"a: " + a.x +"," + a.y)
//        Log.d(TAG,"b: " + b.x +"," + b.y)
//        Log.d(TAG,"c: " + c.x +"," + c.y)
//        Log.d(TAG,"angle: " + angle)

        return angle.toFloat()
    }

}