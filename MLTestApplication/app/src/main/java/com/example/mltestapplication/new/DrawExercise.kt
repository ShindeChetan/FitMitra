package com.example.mltestapplication.new

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewParent
import com.google.mlkit.vision.pose.Pose

//This is a custom view
class DrawExercise(exerciseType: ExerciseType,context: Context,pose: Pose,isActive : Boolean): View(context) {
    var exercise = Factory().getExercise(ExerciseType.DumbellCurl_Type,context, pose,isActive)
    override fun onDraw(canvas: Canvas?) {
        var newCanvas = exercise?.drawing(canvas)
        super.onDraw(newCanvas)
    }
    fun giveIsFinished(): Boolean{
        return exercise?.giveIsFinished() ?: false
    }
}