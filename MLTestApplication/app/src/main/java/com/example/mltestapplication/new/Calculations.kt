package com.example.mltestapplication.new

import android.graphics.PointF

class Calculations {
    //gives the angles be
    fun giveAnglesBetween(startPoint: PointF, commonPoint: PointF, endPoint: PointF): Float{
        var ab:Double = Math.sqrt(Math.pow((startPoint.x - commonPoint.x).toDouble(), 2.0) + Math.pow((startPoint.y - commonPoint.y).toDouble(), 2.0))
        var ac:Double = Math.sqrt(Math.pow((startPoint.x - endPoint.x).toDouble(), 2.0) + Math.pow((startPoint.y - endPoint.y).toDouble(), 2.0))
        var bc:Double = Math.sqrt(Math.pow((commonPoint.x - endPoint.x).toDouble(), 2.0) + Math.pow((commonPoint.y - endPoint.y).toDouble(), 2.0))

        var cosValue = (ab * ab + bc * bc - ac * ac) /( 2 * bc * ab)
        val angle = Math.acos(cosValue) *(180/Math.PI)
        return angle.toFloat()
    }
}