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
        get() = getRealPosition(layoutManager!!.currentPosition)

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
        wrapped.onBindViewHolder(holder, mapPositionToReal(position))
    }

    override fun getItemViewType(position: Int): Int {
        return wrapped.getItemViewType(mapPositionToReal(position))
    }

    override fun getItemCount(): Int {
        return if (wrapped.itemCount == 0) 0 else Integer.MAX_VALUE
    }

    fun getRealPosition(position: Int): Int {
        return mapPositionToReal(position)
    }

    fun getClosestPosition(position: Int): Int {
        val adapterTarget = currentRangeStart + position
        val adapterCurrent = layoutManager!!.currentPosition
        if (adapterTarget == adapterCurrent) {
            return adapterCurrent
        } else if (adapterTarget < adapterCurrent) {
            val adapterTargetNextSet = currentRangeStart + wrapped.itemCount + position
            return if (adapterCurrent - adapterTarget < adapterTargetNextSet - adapterCurrent)
                adapterTarget
            else
                adapterTargetNextSet
        } else {
            val adapterTargetPrevSet = currentRangeStart - wrapped.itemCount + position
            return if (adapterCurrent - adapterTargetPrevSet < adapterTarget - adapterCurrent)
                adapterTargetPrevSet
            else
                adapterTarget
        }
    }

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
        layoutManager!!.scrollToPosition(currentRangeStart + newPosition)
    }

    //TODO: handle proper data set change notifications
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