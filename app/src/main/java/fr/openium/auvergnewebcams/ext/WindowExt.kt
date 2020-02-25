package fr.openium.auvergnewebcams.ext

import android.view.View
import android.view.Window

fun Window.hideSystemUI() {
    decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
}

fun Window.showSystemUI() {
    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
}