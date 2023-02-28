package com.abdallah_abdelazim.loadapp.utils

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.Px


val Context.orientation: Int
    get() = resources.configuration.orientation

fun Context.isPortrait(): Boolean {
    return orientation == Configuration.ORIENTATION_PORTRAIT
}

fun Context.isLandscape(context: Context): Boolean {
    return orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Px
fun Context.dpToPx(dp: Float) = dp * resources.displayMetrics.density

fun Context.pxToDp(@Px px: Float) = px / resources.displayMetrics.density
