package fr.openium.auvergnewebcams.carousel.transform

import android.support.annotation.FloatRange
import android.view.View


/**
 * Created by yarolegovich on 03.03.2017.
 */
class ScaleTransformer : DiscreteScrollItemTransformer {

    private var pivotX: Pivot? = null
    private var pivotY: Pivot? = null
    private var minScale: Float = 0.toFloat()
    private var maxMinDiff: Float = 0.toFloat()

    init {
        pivotX = Pivot.X.CENTER.create()
        pivotY = Pivot.Y.CENTER.create()
        minScale = 0.8f
        maxMinDiff = 0.2f
    }

    override fun transformItem(item: View, position: Float) {
        pivotX!!.setOn(item)
        pivotY!!.setOn(item)
        val closenessToCenter = 1f - Math.abs(position)
        val scale = minScale + maxMinDiff * closenessToCenter
        item.scaleX = scale
        item.scaleY = scale
    }

    class Builder {

        private val transformer: ScaleTransformer
        private var maxScale: Float = 0.toFloat()

        init {
            transformer = ScaleTransformer()
            maxScale = 1f
        }

        fun setMinScale(@FloatRange(from = 0.01) scale: Float): Builder {
            transformer.minScale = scale
            return this
        }

        fun setMaxScale(@FloatRange(from = 0.01) scale: Float): Builder {
            maxScale = scale
            return this
        }

        fun setPivotX(pivotX: Pivot.X): Builder {
            return setPivotX(pivotX.create())
        }

        fun setPivotX(pivot: Pivot): Builder {
            assertAxis(pivot, Pivot.AXIS_X)
            transformer.pivotX = pivot
            return this
        }

        fun setPivotY(pivotY: Pivot.Y): Builder {
            return setPivotY(pivotY.create())
        }

        fun setPivotY(pivot: Pivot): Builder {
            assertAxis(pivot, Pivot.AXIS_Y)
            transformer.pivotY = pivot
            return this
        }

        fun build(): ScaleTransformer {
            transformer.maxMinDiff = maxScale - transformer.minScale
            return transformer
        }

        private fun assertAxis(pivot: Pivot, @Pivot.Axis axis: Int) {
            if (pivot.axis != axis) {
                throw IllegalArgumentException("You passed a Pivot for wrong axis.")
            }
        }
    }
}
