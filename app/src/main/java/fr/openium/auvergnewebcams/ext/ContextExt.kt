import android.content.Context
import android.net.ConnectivityManager

//package fr.openium.auvergnewebcams.ext
//
//import android.content.Context
//import android.content.SharedPreferences
//import android.net.ConnectivityManager
//import android.provider.Settings
//import android.util.TypedValue
//
///**
// * Created by t.coulange on 22/04/16.
// */
val Context.hasNetwork: Boolean
    get() {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager;
        return cm.activeNetworkInfo?.isConnectedOrConnecting ?: false;
    }
//
//val Context.secureId: String
//    get() {
//        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
//    }
//
//fun Context.dip(value: Float, type: Int = TypedValue.COMPLEX_UNIT_DIP): Float {
//    val metrics = getResources().getDisplayMetrics()
//    val resultPix = TypedValue.applyDimension(type, value, metrics)
//    return resultPix
//}
//
//val Context.preferences: SharedPreferences
//    get() = this.applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
//
//fun Context.getBooleanPreference(key: String, default: Boolean = false): Boolean {
//    return this.applicationContext.preferences.getBoolean(key, default)
//}
//
//fun Context.setBooleanPreference(key: String, value: Boolean) {
//    this.applicationContext.preferences.editBoolean(key, value)
//}