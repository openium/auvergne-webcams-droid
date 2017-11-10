package fr.openium.auvergnewebcams.injection

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import com.squareup.leakcanary.RefWatcher

/**
 * Created by t.coulange on 07/12/2016.
 */
object Modules {
    val configModule = Kodein.Module {
        bind<RefWatcher>() with singleton { RefWatcher.DISABLED }
    }

    val serviceModule = Kodein.Module {
        //        bind<Picasso>() with singleton { Picasso.Builder(instance()).build() }
    }
}