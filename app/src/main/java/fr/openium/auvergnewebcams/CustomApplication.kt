package fr.openium.auvergnewebcams

import android.app.Application
import coil.ImageLoader
import com.github.piasy.biv.BigImageViewer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mapbox.common.MapboxOptions
import fr.openium.auvergnewebcams.custom.CoilImageLoader
import fr.openium.auvergnewebcams.di.KoinModules
import fr.openium.auvergnewebcams.di.Modules
import fr.openium.auvergnewebcams.log.FirebaseCrashlyticsTree
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

/**
 * Created by Openium on 19/02/2019.
 */

abstract class CustomApplication : Application() {

    private val imageLoader by inject<ImageLoader>()

    override fun onCreate() {
        super.onCreate()
        
        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN

        startKoin {
            androidContext(this@CustomApplication)
            modules(
                listOf(
                    Modules.configModule,
                    Modules.coilModule,
                    Modules.serviceModule,
                    Modules.preferenceModule,
                    Modules.databaseService,
                    Modules.restModule,
                    Modules.weatherModule,
                    Modules.repositoryModule,
                    KoinModules.vmSplash,
                    KoinModules.vmSettings,
                    KoinModules.vmSection,
                    KoinModules.vmDetails,
                    KoinModules.vmMap,
                    KoinModules.vmMain,
                    KoinModules.vmSearch,
                )
            )
        }

        initTimber()
        initBigImageViewer()
    }

    protected open fun initTimber() {
        val firebaseCrashlytics by inject<FirebaseCrashlytics>()
        Timber.plant(FirebaseCrashlyticsTree(firebaseCrashlytics))
    }

    private fun initBigImageViewer() {
        BigImageViewer.initialize(
            CoilImageLoader(applicationContext, imageLoader)
        )
    }

    companion object {
        const val TAG = "[AW]"
    }
}