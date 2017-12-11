package fr.openium.auvergnewebcams.carousel.transform

import android.view.View

/**
 * Created by yarolegovich on 02.03.2017.
 */

interface DiscreteScrollItemTransformer {
    fun transformItem(item: View, position: Float)
}
