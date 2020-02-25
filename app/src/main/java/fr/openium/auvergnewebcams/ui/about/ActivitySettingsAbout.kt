package fr.openium.auvergnewebcams.ui.about

import android.graphics.Color
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.base.AbstractActivity
import fr.openium.kotlintools.ext.gone
import kotlinx.android.synthetic.main.activity_about.*

/**
 * Created by Openium on 19/02/2019.
 */
class ActivitySettingsAbout : AbstractActivity() {

    override val layoutId: Int = R.layout.activity_about

    override val showHomeAsUp: Boolean = true

    // --- Life cycle
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.animation_from_right, R.anim.animation_to_left)

        webViewAbout.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                if (newProgress == 100) {
                    frameLayoutAboutProgress?.gone()
                }
            }
        }

        webViewAbout.setBackgroundColor(Color.TRANSPARENT)
        webViewAbout.loadUrl("file:///android_asset/about.html")
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }
}