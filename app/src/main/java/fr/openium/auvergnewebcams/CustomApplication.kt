package fr.openium.auvergnewebcams

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.github.piasy.biv.BigImageViewer
import com.google.firebase.crashlytics.FirebaseCrashlytics
import fr.openium.auvergnewebcams.custom.CustomGlideImageLoader
import fr.openium.auvergnewebcams.di.Modules
import fr.openium.auvergnewebcams.log.FirebaseCrashReportingTree
import okhttp3.OkHttpClient
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import timber.log.Timber

/**
 * Created by Openium on 19/02/2019.
 */

abstract class CustomApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        bind<Context>() with singleton { applicationContext }
        import(Modules.configModule)
        import(Modules.glideModule)
        import(Modules.serviceModule)
        import(Modules.preferenceModule)
        import(Modules.databaseService)
        import(Modules.restModule)
        import(Modules.repositoryModule)
    }

    override fun onCreate() {
        super.onCreate()

        plantTimber()

        val foregroundListener by instance<LifecycleObserver>("foregroundListener")
        ProcessLifecycleOwner.get().lifecycle.addObserver(foregroundListener)

        val client by instance<OkHttpClient>("glide")
        initBigImageViewer(client)
    }

    protected open fun plantTimber() {
        Timber.plant(FirebaseCrashReportingTree(FirebaseCrashlytics.getInstance()))
    }

    private fun initBigImageViewer(client: OkHttpClient) {
        // This is needed to be done once at beginning to allow app to use this lib
        BigImageViewer.initialize(CustomGlideImageLoader.with(applicationContext, client))
    }

    companion object {
        const val TAG = "[AW]"
    }
}