package fr.openium.auvergnewebcams.carousel

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by yarolegovich on 28-Apr-17.
 */

class InfiniteScrollAdapter<T : RecyclerView.ViewHolder>(val wrapped: RecyclerView.Adapter<T>) : RecyclerView.Adapter<T>() {

    private var layoutManager: DiscreteScrollLayoutManager? = null

    private var currentRangeStart: Int = 0

    val realCurrentPosition: Int
        get() = getRealPosition(layoutManager?.currentPosition ?: currentRangeStart)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        wrapped.onAttachedToRecyclerView(recyclerView)
        this.wrapped.registerAdapterDataObserver(DataSetChangeDelegate())
        if (recyclerView is DiscreteScrollView) {
            layoutManager = recyclerView.layoutManager as DiscreteScrollLayoutManager
            currentRangeStart = NOT_INITIALIZED
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
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
        wrapped.onBindViewHolder(holder, getRealPosition(position))
    }

    override fun getItemViewType(position: Int): Int {
        return wrapped.getItemViewType(getRealPosition(position))
    }

    override fun getItemCount(): Int {
        return if (wrapped.itemCount == 0) 0 else Integer.MAX_VALUE
    }

    fun getRealPosition(position: Int): Int {
        return getRealRealPosition(position)
    }

    fun getRealRealPosition(position: Int): Int {
        return Math.abs(position % wrapped.itemCount)
    }

    private fun resetRange(newPosition: Int) {
        currentRangeStart = Integer.MAX_VALUE / 2
        if (realCurrentPosition == 0) {
            layoutManager?.scrollToPosition(currentRangeStart + newPosition)
        }
    }

    private inner class DataSetChangeDelegate : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
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

        fun <T : RecyclerView.ViewHolder> wrap(
                adapter: RecyclerView.Adapter<T>): InfiniteScrollAdapter<T> {
            return InfiniteScrollAdapter(adapter)
        }
    }
}