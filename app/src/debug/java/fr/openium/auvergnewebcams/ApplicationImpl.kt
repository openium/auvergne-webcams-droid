package fr.openium.auvergnewebcams

import fr.openium.auvergnewebcams.di.DebugModules
import org.koin.core.context.loadKoinModules
import timber.log.Timber

/**
 * Created by Openium on 19/02/2019.
 */
class ApplicationImpl : CustomApplication() {

    override fun onCreate() {
        super.onCreate()

        loadKoinModules(
            listOf(
                DebugModules.databaseService,
                DebugModules.restModule,
            )
        )
    }

    override fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}