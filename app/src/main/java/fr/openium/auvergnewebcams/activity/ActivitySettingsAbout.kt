package fr.openium.auvergnewebcams.activity

import android.graphics.Color
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import fr.openium.auvergnewebcams.R
import fr.openium.kotlintools.ext.gone
import kotlinx.android.synthetic.main.activity_settings_about.*

/**
 * Created by laura on 04/12/2017.
 */
class ActivitySettingsAbout : AbstractActivity() {

    override val showHomeAsUp: Boolean
        get() = true

    override val layoutId: Int
        get() = R.layout.activity_settings_about

    // --- LIFE CYCLE ---
    // ---------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webViewAbout.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                if (newProgress == 100) {
                    frameLayoutAboutProgress.postDelayed({
                        frameLayoutAboutProgress?.gone()
                    }, 200)
                }
            }
        }

        webViewAbout.setBackgroundColor(Color.TRANSPARENT)
        webViewAbout.loadUrl("file:///android_asset/about.html")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.animation_from_left, R.anim.animation_to_right)
    }
}