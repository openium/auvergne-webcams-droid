package fr.openium.auvergnewebcams.injection

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher

/**
 * Created by t.coulange on 21/03/2017.
 */
object DebugModules {
    val configModule = Kodein.Module {
        bind<RefWatcher>(overrides = true) with singleton { LeakCanary.install(instance()) }
    }

    val serviceModule = Kodein.Module {
        //        bind<GlideRequests>(overrides = true) with singleton { GlideApp.with(instance<Context>()) }
    }
}