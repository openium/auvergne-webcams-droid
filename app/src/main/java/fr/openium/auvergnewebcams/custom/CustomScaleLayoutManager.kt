package fr.openium.auvergnewebcams.custom

import android.content.Context
import android.view.View
import com.leochuan.ScaleLayoutManager

class CustomScaleLayoutManager(context: Context, itemSpace: Int, private var elevation: Float, orientation: Int) :
    ScaleLayoutManager(context, itemSpace, orientation) {

    override fun setViewElevation(itemView: View, targetOffset: Float): Float {
        return itemView.scaleX * elevation
    }
}