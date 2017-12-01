package fr.openium.auvergnewebcams

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.github.piasy.biv.BigImageViewer
import com.github.salomonbrys.kodein.*
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import fr.openium.auvergnewebcams.injection.Modules
import fr.openium.auvergnewebcams.log.CrashReportingTree
import fr.openium.auvergnewebcams.utils.CustomGlideImageLoader
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

abstract class ApplicationBase : Application(), KodeinAware {
    override val kodein by Kodein.lazy {
        bind<Context>() with singleton { applicationContext }
        import(Modules.configModule)
        import(Modules.serviceModule)
    }

    val refWatcher: RefWatcher by lazy.instance<RefWatcher>()

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        initializeCrashlytics()
        Realm.init(this)
        val configuration = RealmConfiguration.Builder()
                .schemaVersion(1)
//                .migration(DatabaseMigration())
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(configuration)
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ProximaNova-Sbold.otf")
                .setFontAttrId(R.attr.fontPath).build())

        BigImageViewer.initialize(CustomGlideImageLoader.with(applicationContext))
    }

    open fun initializeCrashlytics() {
        val core = CrashlyticsCore.Builder().build()
        val crashlytics = Crashlytics.Builder().core(core).build()
        Fabric.with(this, crashlytics)
    }
}
