package com.example.mltestapplication.new

import android.content.Context
import com.google.mlkit.vision.pose.Pose

class Factory {
    fun getExercise(exType:ExerciseType,context: Context,pose: Pose) : Exercise? {
        return when(exType){
            ExerciseType.DumbellCurl_Type -> DumbellCurls(context,pose)
            else -> null
        }
    }
}