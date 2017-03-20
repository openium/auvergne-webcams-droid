package fr.openium.auvergnewebcams

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import fr.openium.auvergnewebcams.injection.Modules
import fr.openium.auvergnewebcams.log.CrashReportingTree
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber

class ApplicationAW : Application(), KodeinAware {

    override val kodein by Kodein.lazy {
        import(Modules.configModule)
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
                .build()
        Realm.setDefaultConfiguration(configuration)
//        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setFontAttrId(R.attr.fontPath).build())
    }

    open fun initializeCrashlytics() {
        val core = CrashlyticsCore.Builder().disabled(!BuildConfig.DEBUG).build()
        val crashlytics = Crashlytics.Builder().core(core).build()
        Fabric.with(this, crashlytics)
    }
}
