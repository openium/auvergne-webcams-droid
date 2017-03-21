package fr.openium.auvergnewebcams

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.lazy
import fr.openium.auvergnewebcams.injection.DebugModules
import io.fabric.sdk.android.Fabric

/**
 * Created by t.coulange on 20/03/2017.
 */
class ApplicationImpl : ApplicationBase() {
    override val kodein: Kodein by Kodein.lazy {
        extend(super.kodein)
        import(DebugModules.configModule, true)
        import(DebugModules.serviceModule, true)
    }

    override fun onCreate() {
        super.onCreate()
    }


    override fun initializeCrashlytics() {
        val core = CrashlyticsCore.Builder().disabled(true).build()
        val crashlytics = Crashlytics.Builder().core(core).build()
        Fabric.with(this, crashlytics)
    }

}