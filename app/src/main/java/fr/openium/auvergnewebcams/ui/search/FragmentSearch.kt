package fr.openium.auvergnewebcams.ui.search

import android.os.Bundle
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragment
import kotlinx.android.synthetic.main.fragment_search.*


/**
 * Created by Openium on 19/02/2019.
 */
class FragmentSearch : AbstractFragment() {

    override val layoutId: Int = R.layout.fragment_search

    // --- Life cycle
    // ---------------------------------------------------

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        textViewSearch.requestFocus()

        setListener()
    }

    // --- Methods
    // ---------------------------------------------------

    private fun setListener() {

    }
}