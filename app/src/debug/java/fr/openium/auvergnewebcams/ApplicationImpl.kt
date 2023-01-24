package fr.openium.auvergnewebcams

import fr.openium.auvergnewebcams.di.DebugModules
import org.kodein.di.Copy
import org.kodein.di.Kodein
import timber.log.Timber

/**
 * Created by Openium on 19/02/2019.
 */
class ApplicationImpl : CustomApplication() {

    override val kodein = Kodein.lazy {
        extend(super.kodein, copy = Copy.All)
        import(DebugModules.configModule, true)
        import(DebugModules.restModule, true)
        import(DebugModules.repositoryModule, true)
        import(DebugModules.serviceModule, true)
        import(DebugModules.databaseService, true)
    }

    override fun plantTimber() {
        Timber.plant(Timber.DebugTree())
    }
}