package fr.openium.auvergnewebcams

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import fr.openium.auvergnewebcams.injection.DebugModules
import io.fabric.sdk.android.Fabric
import io.realm.RealmConfiguration
import org.kodein.di.Copy
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import timber.log.Timber

/**
 * Created by Openium on 20/03/2018.
 */
class ApplicationImpl : CustomApplication() {

    override val kodein = Kodein.lazy {
        extend(super.kodein, copy = Copy.All)
        import(DebugModules.configModule, true)
        import(DebugModules.restModule, true)
        import(DebugModules.serviceModule, true)
    }

    val mock by instance<Boolean>("mock")

    override fun onCreate() {
        super.onCreate()
    }

    override fun plantTimber() {
        Timber.plant(Timber.DebugTree())
    }

    override fun initRealm(builder: RealmConfiguration.Builder): RealmConfiguration.Builder {
        val newBuilder = super.initRealm(builder)
        return if (mock) newBuilder.inMemory() else newBuilder
    }

    override fun initializeCrashlytics() {
        val core = CrashlyticsCore.Builder().disabled(true).build()
        val crashlytics = Crashlytics.Builder().core(core).build()
        Fabric.with(this, crashlytics)
    }
}