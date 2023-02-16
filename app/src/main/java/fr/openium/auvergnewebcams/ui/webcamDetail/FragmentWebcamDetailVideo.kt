package fr.openium.auvergnewebcams.ui.webcamDetail

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.Util
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragmentWebcam
import fr.openium.auvergnewebcams.enums.WebcamType
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.ext.jsonKey
import fr.openium.auvergnewebcams.utils.LoadWebCamUtils
import fr.openium.kotlintools.ext.getColorCompat
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.goneWithAnimationCompat
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.showWithAnimationCompat
import fr.openium.kotlintools.ext.snackbar
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.footer_webcam_detail.textViewWebcamDetailLowQualityOnly
import kotlinx.android.synthetic.main.fragment_webcam_video.linearLayoutWebcamVideoDetailContent
import kotlinx.android.synthetic.main.fragment_webcam_video.playerViewWebcamVideo
import timber.log.Timber

/**
 * Created by Openium on 19/02/2019.
 */
class FragmentWebcamDetailVideo : AbstractFragmentWebcam() {

    override fun isVideo(): Boolean {
        return true
    }

}