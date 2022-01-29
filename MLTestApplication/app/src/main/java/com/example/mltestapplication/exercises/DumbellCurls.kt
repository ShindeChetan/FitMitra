package com.example.mltestapplication.exercises

import android.graphics.PointF

data class DumbellCurls(val jointsToDisplay : MutableMap<String, PointF>, val bodypartsLinesToDisplay : MutableList<Pair<PointF, PointF>>, val redJoints : MutableMap<String , PointF>, val repPosition : String? ,val errorText : MutableList<String>)