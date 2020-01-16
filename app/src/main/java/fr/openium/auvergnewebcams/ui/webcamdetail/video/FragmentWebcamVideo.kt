package fr.openium.auvergnewebcams.ui.webcamdetail.video

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.snackbar.Snackbar
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractFragmentWebcam
import fr.openium.auvergnewebcams.utils.PreferencesAW
import fr.openium.kotlintools.ext.gone
import fr.openium.kotlintools.ext.hide
import fr.openium.kotlintools.ext.show
import fr.openium.kotlintools.ext.snackbar
import kotlinx.android.synthetic.main.footer_webcam_detail.*
import kotlinx.android.synthetic.main.fragment_webcam_video.*

/**
 * Created by laura on 01/12/2017.
 */
class FragmentWebcamVideo : AbstractFragmentWebcam() {

    override val layoutId: Int
        get() = R.layout.fragment_webcam_video

    private lateinit var player: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory

    private var url: String? = null

    // --- Life cycle
    // ---------------------------------------------------

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT && webcam.viewsurfHD.isNullOrEmpty()) {
            textViewWebcamLowQualityOnly.show()
        } else {
            textViewWebcamLowQualityOnly.gone()
        }
    }

    override fun onStop() {
        super.onStop()
        player.release()
    }

    // --- Methods
    // ---------------------------------------------------

    override fun initWebcam() {
        val url = webcam.getUrlForWebcam(PreferencesAW.isWebcamsHighQuality(requireContext()), true)

        player = SimpleExoPlayer.Builder(requireContext()).build()
        mediaDataSourceFactory = DefaultDataSourceFactory(requireContext(), Util.getUserAgent(requireContext(), "mpAW"))

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(Uri.parse(url))

        player.addListener(object : Player.EventListener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    progressBarWebcamVideo.hide()
                } else {
                    progressBarWebcamVideo.show()
                }

                imageViewWebcamVideoLoadingError.gone()
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                imageViewWebcamVideoLoadingError.show()
            }
        })

        player.prepare(mediaSource, false, false)
        player.playWhenReady = true

        playerViewWebcamVideo.setShutterBackgroundColor(Color.TRANSPARENT)
        playerViewWebcamVideo.player = player
        playerViewWebcamVideo.showController()
        playerViewWebcamVideo.requestFocus()
    }

    override fun shareWebCam() {
        val url = webcam.getUrlForWebcam(canBeHD = true, canBeVideo = true)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, String.format("%s \n%s", webcam.title, url))
            putExtra(Intent.EXTRA_SUBJECT, webcam.title)
        }

        val chooser = Intent.createChooser(intent, getString(R.string.generic_chooser))

        chooser.resolveActivity(requireActivity().packageManager)?.also {
            startActivity(chooser)
        } ?: snackbar(R.string.generic_no_application_for_action, Snackbar.LENGTH_SHORT)
    }

    override fun saveWebcam() {
        // TODO
    }

    override fun refreshWebcam() {
        // TODO
    }

//    override fun showProgress() {
//        progressBarWebcamVideo.show()
//    }
}