package fr.openium.auvergnewebcams

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import fr.openium.auvergnewebcams.injection.Modules
import fr.openium.auvergnewebcams.log.CrashReportingTree
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import timber.log.Timber

/**
 * Created by Openium on 20/03/2018.
 */

abstract class CustomApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        bind<Context>() with singleton { applicationContext }
        import(Modules.configModule)
        import(Modules.restModule)
        import(Modules.serviceModule)
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val builder = RealmConfiguration.Builder()
        val config = initRealm(builder).build()
        Realm.setDefaultConfiguration(config)

        initializeCrashlytics()

        plantTimber()
    }

    open fun initRealm(builder: RealmConfiguration.Builder): RealmConfiguration.Builder {
        return builder.deleteRealmIfMigrationNeeded()
    }

    open fun initializeCrashlytics() {
        Fabric.with(applicationContext, Crashlytics(), Answers())
    }

    protected open fun plantTimber() {
        Timber.plant(CrashReportingTree())
    }
}
