package fr.openium.auvergnewebcams.carousel

import android.graphics.Point
import android.support.v7.widget.RecyclerView

/**
 * Created by yarolegovich on 16.03.2017.
 */
enum class Orientation {

    HORIZONTAL {
        override fun createHelper(): Helper {
            return HorizontalHelper()
        }
    },
    VERTICAL {
        override fun createHelper(): Helper {
            return VerticalHelper()
        }
    };

    //Package private
    internal abstract fun createHelper(): Helper

    internal interface Helper {

        fun getViewEnd(recyclerWidth: Int, recyclerHeight: Int): Int

        fun getDistanceToChangeCurrent(childWidth: Int, childHeight: Int): Int

        fun setCurrentViewCenter(recyclerCenter: Point, scrolled: Int, outPoint: Point)

        fun shiftViewCenter(direction: Direction, shiftAmount: Int, outCenter: Point)

        fun getFlingVelocity(velocityX: Int, velocityY: Int): Int

        fun getPendingDx(pendingScroll: Int): Int

        fun getPendingDy(pendingScroll: Int): Int

        fun offsetChildren(amount: Int, lm: RecyclerView.LayoutManager)

        fun getDistanceFromCenter(center: Point, viewCenterX: Int, viewCenterY: Int): Float

        fun isViewVisible(center: Point, halfWidth: Int, halfHeight: Int, endBound: Int, extraSpace: Int): Boolean

        fun hasNewBecomeVisible(lm: DiscreteScrollLayoutManager): Boolean

        fun canScrollVertically(): Boolean

        fun canScrollHorizontally(): Boolean
    }

    protected class HorizontalHelper : Helper {

        override fun getViewEnd(recyclerWidth: Int, recyclerHeight: Int): Int {
            return recyclerWidth
        }

        override fun getDistanceToChangeCurrent(childWidth: Int, childHeight: Int): Int {
            return childWidth
        }

        override fun setCurrentViewCenter(recyclerCenter: Point, scrolled: Int, outPoint: Point) {
            val newX = recyclerCenter.x - scrolled
            outPoint.set(newX, recyclerCenter.y)
        }

        override fun shiftViewCenter(direction: Direction, shiftAmount: Int, outCenter: Point) {
            val newX = outCenter.x + direction.applyTo(shiftAmount)
            outCenter.set(newX, outCenter.y)
        }

        override fun isViewVisible(viewCenter: Point, halfWidth: Int, halfHeight: Int, endBound: Int, extraSpace: Int): Boolean {
            val viewLeft = viewCenter.x - halfWidth
            val viewRight = viewCenter.x + halfWidth
            return viewLeft < endBound + extraSpace && viewRight > -extraSpace
        }

        override fun hasNewBecomeVisible(lm: DiscreteScrollLayoutManager): Boolean {
            val firstChild = lm.firstChild
            val lastChild = lm.lastChild
            val leftBound = -lm.extraLayoutSpace
            val rightBound = lm.width + lm.extraLayoutSpace
            val isNewVisibleFromLeft = lm.getDecoratedLeft(firstChild) > leftBound && lm.getPosition(firstChild) > 0
            val isNewVisibleFromRight = lm.getDecoratedRight(lastChild) < rightBound && lm.getPosition(lastChild) < lm.itemCount - 1
            return isNewVisibleFromLeft || isNewVisibleFromRight
        }

        override fun offsetChildren(amount: Int, lm: RecyclerView.LayoutManager) {
            lm.offsetChildrenHorizontal(amount)
        }

        override fun getDistanceFromCenter(center: Point, viewCenterX: Int, viewCenterY: Int): Float {
            return (viewCenterX - center.x).toFloat()
        }

        override fun getFlingVelocity(velocityX: Int, velocityY: Int): Int {
            return velocityX
        }

        override fun canScrollHorizontally(): Boolean {
            return true
        }

        override fun canScrollVertically(): Boolean {
            return false
        }

        override fun getPendingDx(pendingScroll: Int): Int {
            return pendingScroll
        }

        override fun getPendingDy(pendingScroll: Int): Int {
            return 0
        }
    }


    protected class VerticalHelper : Helper {

        override fun getViewEnd(recyclerWidth: Int, recyclerHeight: Int): Int {
            return recyclerHeight
        }

        override fun getDistanceToChangeCurrent(childWidth: Int, childHeight: Int): Int {
            return childHeight
        }

        override fun setCurrentViewCenter(recyclerCenter: Point, scrolled: Int, outPoint: Point) {
            val newY = recyclerCenter.y - scrolled
            outPoint.set(recyclerCenter.x, newY)
        }

        override fun shiftViewCenter(direction: Direction, shiftAmount: Int, outCenter: Point) {
            val newY = outCenter.y + direction.applyTo(shiftAmount)
            outCenter.set(outCenter.x, newY)
        }

        override fun offsetChildren(amount: Int, lm: RecyclerView.LayoutManager) {
            lm.offsetChildrenVertical(amount)
        }

        override fun getDistanceFromCenter(center: Point, viewCenterX: Int, viewCenterY: Int): Float {
            return (viewCenterY - center.y).toFloat()
        }

        override fun isViewVisible(center: Point, halfWidth: Int, halfHeight: Int, endBound: Int, extraSpace: Int): Boolean {
            val viewTop = center.y - halfHeight
            val viewBottom = center.y + halfHeight
            return viewTop < endBound + extraSpace && viewBottom > -extraSpace
        }

        override fun hasNewBecomeVisible(lm: DiscreteScrollLayoutManager): Boolean {
            val firstChild = lm.firstChild
            val lastChild = lm.lastChild
            val topBound = -lm.extraLayoutSpace
            val bottomBound = lm.height + lm.extraLayoutSpace
            val isNewVisibleFromTop = lm.getDecoratedTop(firstChild) > topBound && lm.getPosition(firstChild) > 0
            val isNewVisibleFromBottom = lm.getDecoratedBottom(lastChild) < bottomBound && lm.getPosition(lastChild) < lm.itemCount - 1
            return isNewVisibleFromTop || isNewVisibleFromBottom
        }

        override fun getFlingVelocity(velocityX: Int, velocityY: Int): Int {
            return velocityY
        }

        override fun canScrollHorizontally(): Boolean {
            return false
        }

        override fun canScrollVertically(): Boolean {
            return true
        }

        override fun getPendingDx(pendingScroll: Int): Int {
            return 0
        }

        override fun getPendingDy(pendingScroll: Int): Int {
            return pendingScroll
        }
    }
}
