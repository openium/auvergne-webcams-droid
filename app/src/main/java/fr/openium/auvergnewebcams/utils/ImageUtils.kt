package fr.openium.auvergnewebcams.utils

import android.content.Context
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.entity.Section

/**
 * Created by Openium on 19/02/2019.
 */
object ImageUtils {

    fun getImageResourceAssociatedToSection(context: Context, item: Section): Int {
        val imageName = "categ_" + item.imageName?.replace("-", "_")

        // Return 0 if there is no image associated
        val imageResourceID = context.resources.getIdentifier(imageName, "drawable", context.packageName)

        return if (imageResourceID != 0) imageResourceID else R.drawable.categ_pdd_landscape
    }
}