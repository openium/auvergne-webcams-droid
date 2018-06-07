package fr.openium.auvergnewebcams

import android.content.Context
import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import com.github.piasy.biv.BigImageViewer
import com.github.salomonbrys.kodein.*
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import fr.openium.auvergnewebcams.injection.Modules
import fr.openium.auvergnewebcams.log.CrashReportingTree
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.utils.CustomGlideImageLoader
import io.fabric.sdk.android.Fabric
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import okhttp3.Response
import timber.log.Timber
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


abstract class ApplicationBase : MultiDexApplication(), KodeinAware {
    override val kodein by Kodein.lazy {

        bind<Context>() with singleton { applicationContext }
        import(Modules.configModule)
        import(Modules.serviceModule)
        import(Modules.restModule)
        import(Modules.weatherModule)
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

        initializeFabric()
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

        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException || e is InterruptedException) {
                Timber.e(e)
                Crashlytics.logException(e)
            }
        }

        val client = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addInterceptor {
                    // Timber.e("REQUEST ${it.request().url().toString()}")

                    val response: Response = it.proceed(it.request())
                    if (response.header("Last-Modified") != null) {
                        val url = it.request().url().toString()

                        val argsSplit = url.split("/")
                        val urlMedia: String
                        if (!argsSplit.isEmpty()) {
                            urlMedia = argsSplit.lastOrNull()?.replace(".jpg", "") ?: ""
                        } else {
                            urlMedia = ""
                        }

                        Realm.getDefaultInstance().use {
                            it.executeTransaction {
                                val webcam: Webcam?
                                if (urlMedia.isEmpty()) {
                                    webcam = it.where(Webcam::class.java)
                                            .contains(Webcam::imageLD.name, url)
                                            .or()
                                            .contains(Webcam::imageHD.name, url)
                                            .findFirst()
                                } else {
                                    webcam = it.where(Webcam::class.java)
                                            .contains(Webcam::mediaViewSurfHD.name, urlMedia)
                                            .or()
                                            .contains(Webcam::mediaViewSurfLD.name, urlMedia)
                                            .or()
                                            .contains(Webcam::imageLD.name, url)
                                            .or()
                                            .contains(Webcam::imageHD.name, url)
                                            .findFirst()
                                }

                                if (webcam != null) {
                                    val lastModified = response.header("Last-Modified")
                                    if (!lastModified.isNullOrEmpty()) {
                                        val dateFormat = SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US)
                                        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                                        val newTime = dateFormat.parse(lastModified).time
//                                    Timber.e("APP update date $newTime   ${webcam.title}")
                                        if (webcam.lastUpdate == null || newTime != webcam.lastUpdate!!) {
                                            webcam.lastUpdate = newTime
                                        }
                                    }
                                }
                            }
                        }

                    }

                    response
                }
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build()
        BigImageViewer.initialize(CustomGlideImageLoader.with(applicationContext, client))
    }

    open fun initializeFabric() {
        val core = CrashlyticsCore.Builder().build()
        val crashlytics = Crashlytics.Builder().core(core).build()
        val answer = Answers()
        Fabric.with(this, crashlytics, answer)
    }
}
