package fr.openium.auvergnewebcams.di

import android.content.Context
import androidx.lifecycle.LifecycleObserver
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.event.ForegroundBackgroundListener
import fr.openium.auvergnewebcams.model.CustomClient
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.ApiHelper
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import org.kodein.di.Kodein
import org.kodein.di.generic.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by Openium on 19/02/2019.
 */

object Modules {

    val configModule = Kodein.Module("Config Module") {
        constant("mock") with false
    }

    val serviceModule = Kodein.Module("Service Module") {
        bind<Glide>() with singleton {
            Glide.get(instance())
        }

        bind<LifecycleObserver>("foregroundListener") with provider {
            ForegroundBackgroundListener(instance())
        }
    }

    val preferenceModule = Kodein.Module("Preference Module") {
        bind<PreferencesUtils>() with singleton { PreferencesUtils(instance()) }
    }

    val databaseService = Kodein.Module("Database Module") {
        bind<CustomClient>() with provider {
            CustomClient.getInstance(instance())
        }
    }

    val restModule = Kodein.Module("REST Module") {
        bind<Cache>() with provider {
            val cacheSize = (20 * 1024 * 1024).toLong() // 20 MiB
            Cache(instance<Context>().cacheDir, cacheSize)
        }

        bind<HttpUrl>() with singleton {
            instance<Context>().getString(R.string.url_dev).toHttpUrlOrNull()!!
        }

        bind<OkHttpClient>() with provider {
            OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .cache(instance())
                .build()
        }

        bind<Retrofit>() with singleton {
            Retrofit.Builder()
                .baseUrl(instance<HttpUrl>())
                .client(instance())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }

        bind<Gson>() with singleton {
            GsonBuilder().setLenient().serializeSpecialFloatingPointValues().create()
        }

        bind<AWApi>() with singleton {
            instance<Retrofit>().create(AWApi::class.java)
        }

        bind<ApiHelper>() with singleton {
            ApiHelper(instance(), instance())
        }
    }

    val repositoryModule = Kodein.Module("Repository Module") {
        bind<SectionRepository>() with provider { SectionRepository(instance(), instance(), instance()) }
        bind<WebcamRepository>() with provider { WebcamRepository(instance()) }
    }
}
