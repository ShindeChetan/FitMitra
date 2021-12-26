package com.example.mltestapplication.new

import android.graphics.Canvas
import android.graphics.PointF

interface Exercise {
    abstract fun drawing(canvas: Canvas?): Canvas?
    abstract fun giveIsFinished(): Boolean
    val type : ExerciseType
    var jointsToDisplay : MutableMap<String, PointF>
    var linesToDisplay : MutableList<Pair<PointF,PointF>>

//    fun draw()
}