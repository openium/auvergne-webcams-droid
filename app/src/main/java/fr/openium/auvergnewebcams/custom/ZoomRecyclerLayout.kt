package fr.openium.auvergnewebcams.custom

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

class ZoomRecyclerLayout(context: Context) : LinearLayoutManager(context) {

    companion object {
        const val originalScale = 1f
        const val minScale = 0.7f
        private const val scaleDistanceRatio = 1f

        const val originalAlpha = 1f
        const val minAlpha = 0.5f
        private const val alphaDistanceRatio = 1f
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
//        scrollToPositionWithOffset(
//            Integer.MAX_VALUE / 2,
//            dip(view?.context, 50f).toInt()
//        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        layoutViews()
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return if (orientation == VERTICAL) {
            val scrolled = super.scrollVerticallyBy(dy, recycler, state)
            layoutViews()
            scrolled
        } else 0
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        return if (orientation == HORIZONTAL) {
            val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
            layoutViews()
            scrolled
        } else 0
    }

    private fun layoutViews() {
        val midpoint = if (orientation == HORIZONTAL) width / 2f else height / 2f

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childMidpoint = (getDecoratedRight(child!!) + getDecoratedLeft(child)) / 2f

            setScale(midpoint, childMidpoint, child)
            setAlpha(midpoint, childMidpoint, child)
        }
    }

    private fun setScale(midpoint: Float, childMidpoint: Float, child: View) {
        val d1 = scaleDistanceRatio * midpoint
        val d = d1.coerceAtMost(abs(midpoint - childMidpoint))

        val diffBetweenOriginAndShrinkSize = 1f - (originalScale - minScale)
        val scale = originalScale + (diffBetweenOriginAndShrinkSize - originalScale) * d / d1
        child.apply {
            scaleX = scale
            scaleY = scale
        }
    }

    private fun setAlpha(midpoint: Float, childMidpoint: Float, child: View) {
        val d1 = alphaDistanceRatio * midpoint
        val d = d1.coerceAtMost(abs(midpoint - childMidpoint))

        val diffBetweenOriginAndShrinkAlpha = 1f - (originalAlpha - minAlpha)
        child.alpha = originalAlpha + (diffBetweenOriginAndShrinkAlpha - originalAlpha) * d / d1
    }
}