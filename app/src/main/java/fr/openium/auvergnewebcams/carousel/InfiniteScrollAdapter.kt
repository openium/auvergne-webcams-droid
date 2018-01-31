package fr.openium.auvergnewebcams.carousel

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by yarolegovich on 28-Apr-17.
 */

class InfiniteScrollAdapter<T : RecyclerView.ViewHolder>(private val wrapped: RecyclerView.Adapter<T>) : RecyclerView.Adapter<T>() {

    private var layoutManager: DiscreteScrollLayoutManager? = null

    private var currentRangeStart: Int = 0

    val realItemCount: Int
        get() = wrapped.itemCount

    val realCurrentPosition: Int
        get() = getRealPosition(layoutManager?.currentPosition ?: currentRangeStart)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        wrapped.onAttachedToRecyclerView(recyclerView)
        this.wrapped.registerAdapterDataObserver(DataSetChangeDelegate())
        if (recyclerView is DiscreteScrollView) {
            layoutManager = recyclerView.layoutManager as DiscreteScrollLayoutManager
            currentRangeStart = NOT_INITIALIZED
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        wrapped.onDetachedFromRecyclerView(recyclerView)
        layoutManager = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): T {
        if (currentRangeStart == NOT_INITIALIZED) {
            resetRange(0)
        }
        return wrapped.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: T, position: Int) {
        wrapped.onBindViewHolder(holder, getRealRealPosition(position))
    }

    override fun getItemViewType(position: Int): Int {
        return wrapped.getItemViewType(getRealRealPosition(position))
    }

    override fun getItemCount(): Int {
        return if (wrapped.itemCount == 0) 0 else Integer.MAX_VALUE
    }

    fun getRealPosition(position: Int): Int {
        return getRealRealPosition(position)
//        return getRealRealPosition(position)
    }

    fun getRealRealPosition(position: Int): Int {
        val newPosition = (position - currentRangeStart)

        if (newPosition == 0) {
            resetRange(0)
            return 0
        }
        if (newPosition < 0) {
            return Math.abs(position % wrapped.itemCount)
        }
        if (newPosition == wrapped.itemCount) {
//            Timber.e("position 0")
            return 0
        }
        if (newPosition >= wrapped.itemCount) {
            val modulo = position % currentRangeStart
            val resultTemp = modulo / wrapped.itemCount
            val resultTempTemp = resultTemp * wrapped.itemCount
            return (position - currentRangeStart) - resultTempTemp
        }

        return newPosition
    }

//    fun getClosestPosition(position: Int): Int {
//        val adapterTarget = currentRangeStart + position
//        val adapterCurrent = layoutManager!!.currentPosition
//        if (adapterTarget == adapterCurrent) {
//            return adapterCurrent
//        } else if (adapterTarget < adapterCurrent) {
//            val adapterTargetNextSet = currentRangeStart + wrapped.itemCount + position
//            return if (adapterCurrent - adapterTarget < adapterTargetNextSet - adapterCurrent)
//                adapterTarget
//            else
//                adapterTargetNextSet
//        } else {
//            val adapterTargetPrevSet = currentRangeStart - wrapped.itemCount + position
//            return if (adapterCurrent - adapterTargetPrevSet < adapterTarget - adapterCurrent)
//                adapterTargetPrevSet
//            else
//                adapterTarget
//        }
//    }

    private fun mapPositionToReal(position: Int): Int {
        val newPosition = position - currentRangeStart
        if (newPosition >= wrapped.itemCount) {
            currentRangeStart += wrapped.itemCount
            if (Integer.MAX_VALUE - currentRangeStart <= RESET_BOUND) {
                resetRange(0)
            }
            return 0
        } else if (newPosition < 0) {
            currentRangeStart -= wrapped.itemCount
            if (currentRangeStart <= RESET_BOUND) {
                resetRange(wrapped.itemCount - 1)
            }
            return wrapped.itemCount - 1
        } else {
            return newPosition
        }
    }

    private fun resetRange(newPosition: Int) {
        currentRangeStart = Integer.MAX_VALUE / 2
        layoutManager?.scrollToPosition(currentRangeStart + newPosition)
    }

    private inner class DataSetChangeDelegate : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            resetRange(realCurrentPosition)
            notifyDataSetChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            onChanged()
        }
    }

    companion object {

        private val NOT_INITIALIZED = -1
        private val RESET_BOUND = 100

        fun <T : RecyclerView.ViewHolder> wrap(
                adapter: RecyclerView.Adapter<T>): InfiniteScrollAdapter<T> {
            return InfiniteScrollAdapter(adapter)
        }
    }
}