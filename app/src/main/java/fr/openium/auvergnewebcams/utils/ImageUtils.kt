package fr.openium.auvergnewebcams.utils

import android.content.Context
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.Section

/**
 * Created by Skyle on 19/02/2019.
 */
object ImageUtils {

    //Return 0 if there is no image associated
    fun getImageResourceAssociatedToSection(context: Context, item: Section): Int {
        val imageName = item.imageName?.replace("-", "_") ?: ""

        val imageResourceID = context.resources.getIdentifier(imageName, "drawable", context.packageName)

        return if (imageResourceID != 0) {
            imageResourceID
        } else {
            R.drawable.pdd_landscape
        }
    }
}