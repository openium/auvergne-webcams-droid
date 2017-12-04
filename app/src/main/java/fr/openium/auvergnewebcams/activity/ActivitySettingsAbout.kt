package fr.openium.auvergnewebcams.activity

import android.graphics.Color
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.gone
import kotlinx.android.synthetic.main.activity_settings_about.*

/**
 * Created by laura on 04/12/2017.
 */
class ActivitySettingsAbout : AbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webviewAbout.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                framelayout_progress?.gone()
            }
        }

        webviewAbout.setBackgroundColor(Color.TRANSPARENT)
        webviewAbout.loadUrl("file:///android_asset/about.html")
    }

    override val layoutId: Int
        get() = R.layout.activity_settings_about

    override val showHomeAsUp: Boolean
        get() = true

}