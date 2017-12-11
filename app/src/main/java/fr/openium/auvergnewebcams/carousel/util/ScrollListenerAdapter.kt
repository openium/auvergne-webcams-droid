package fr.openium.auvergnewebcams.carousel.util

import android.support.v7.widget.RecyclerView
import fr.openium.auvergnewebcams.carousel.DiscreteScrollView

/**
 * Created by yarolegovich on 16.03.2017.
 */
class ScrollListenerAdapter<T : RecyclerView.ViewHolder>(private val adapter: DiscreteScrollView.ScrollListener<T>) : DiscreteScrollView.ScrollStateChangeListener<T> {

    override fun onScrollStart(currentItemHolder: T, adapterPosition: Int) {

    }

    override fun onScrollEnd(currentItemHolder: T, adapterPosition: Int) {

    }

    override fun onScroll(scrollPosition: Float,
                          currentIndex: Int, newIndex: Int,
                          currentHolder: T?, newCurrentHolder: T?) {
        adapter.onScroll(scrollPosition, currentIndex, newIndex, currentHolder, newCurrentHolder)
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj is ScrollListenerAdapter<*>) {
            adapter == obj.adapter
        } else {
            super.equals(obj)
        }
    }
}
