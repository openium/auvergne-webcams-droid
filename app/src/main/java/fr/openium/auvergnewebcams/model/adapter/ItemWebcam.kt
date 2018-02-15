package fr.openium.auvergnewebcams.model.adapter

import fr.openium.auvergnewebcams.model.Webcam

/**
 * Created by laura on 20/03/2017.
 */
data class ItemWebcam(val nameSection: String, val imageSection: String, val nbWebcams: Int, val webcam: List<Webcam>)