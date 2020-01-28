package fr.openium.auvergnewebcams.ext

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import fr.openium.auvergnewebcams.custom.SnapOnScrollListener

fun RecyclerView.attachSnapHelperWithListener(
    snapHelper: SnapHelper,
    behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
    onSnapPositionChangeListener: SnapOnScrollListener.OnSnapPositionChangeListener
) {
    snapHelper.attachToRecyclerView(this)
    addOnScrollListener(SnapOnScrollListener(snapHelper, behavior, onSnapPositionChangeListener))
}