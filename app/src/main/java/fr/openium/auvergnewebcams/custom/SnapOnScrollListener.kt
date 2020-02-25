package fr.openium.auvergnewebcams.custom

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

class SnapOnScrollListener(
    private val snapHelper: SnapHelper,
    var behavior: Behavior = Behavior.NOTIFY_ON_SCROLL,
    var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null
) : RecyclerView.OnScrollListener() {

    enum class Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE
    }

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL) {
            maybeNotifySnapPositionChange(recyclerView)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE && newState == RecyclerView.SCROLL_STATE_IDLE) {
            maybeNotifySnapPositionChange(recyclerView)
        }
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        recyclerView.layoutManager?.also {
            snapHelper.findSnapView(recyclerView.layoutManager)?.also { snapView ->
                recyclerView.layoutManager?.getPosition(snapView)?.also { snapPos ->
                    if (snapPosition != snapPos) {
                        onSnapPositionChangeListener?.onSnapPositionChange(snapPos)
                        snapPosition = snapPos
                    }
                }
            }
        }
    }

    interface OnSnapPositionChangeListener {

        fun onSnapPositionChange(position: Int)
    }
}