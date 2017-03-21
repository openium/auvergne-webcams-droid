package fr.openium.auvergnewebcams

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric

/**
 * Created by t.coulange on 20/03/2017.
 */
class ApplicationImpl : ApplicationBase() {
    override fun onCreate() {
        super.onCreate()
    }


    override fun initializeCrashlytics() {
        val core = CrashlyticsCore.Builder().disabled(true).build()
        val crashlytics = Crashlytics.Builder().core(core).build()
        Fabric.with(this, crashlytics)
    }

}