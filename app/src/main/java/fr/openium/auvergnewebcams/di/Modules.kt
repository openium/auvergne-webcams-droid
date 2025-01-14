package fr.openium.auvergnewebcams.di

import android.content.Context
import android.util.Log
import coil.ImageLoader
import coil.decode.VideoFrameDecoder
import coil.util.DebugLogger
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.GsonBuilder
import fr.openium.auvergnewebcams.BuildConfig
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.custom.CacheInterceptor
import fr.openium.auvergnewebcams.custom.LastUpdateDateInterceptor
import fr.openium.auvergnewebcams.event.ForegroundBackgroundListener
import fr.openium.auvergnewebcams.model.AWClient
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.AWWeatherApi
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by Openium on 19/02/2019.
 */
object Modules {

    val configModule = module {
        single {
            FirebaseCrashlytics.getInstance().apply {
                setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
            }
        }
    }

    val serviceModule = module {
        single(named("foregroundListener")) {
            ForegroundBackgroundListener(get())
        }

        single {
            DateUtils(get())
        }
    }

    val coilModule = module {
        single {
            ImageLoader.Builder(get())
                .okHttpClient(get<OkHttpClient>(named("COIL_OK_HTTP")))
                .crossfade(true)
                .allowRgb565(true)
                .logger(DebugLogger(Log.VERBOSE))
                .components {
                    add(VideoFrameDecoder.Factory())
                }.build()
        }

        single(named("COIL_OK_HTTP")) {
            OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addInterceptor(get<LastUpdateDateInterceptor>(named("COIL_LAST_UPDATE_INTERCEPTOR")))
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        single(named("COIL_LAST_UPDATE_INTERCEPTOR")) {
            LastUpdateDateInterceptor(get())
        }
    }

    val preferenceModule = module {
        single {
            PreferencesUtils(get())
        }
    }

    val databaseService = module {
        single {
            AWClient.getInstance(get())
        }
    }

    val restModule = module {
        single {
            val cacheSize = (20 * 1024 * 1024).toLong() // 20 MiB
            Cache(get<Context>().cacheDir, cacheSize)
        }

        single {
            get<Context>().getString(R.string.url_dev).toHttpUrlOrNull()!!
        }

        single {
            OkHttpClient.Builder()
                .readTimeout(20, TimeUnit.SECONDS)
                .cache(get())
                .build()
        }

        single {
            Retrofit.Builder()
                .baseUrl(get<HttpUrl>())
                .client(get())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }

        single {
            GsonBuilder().setLenient().serializeSpecialFloatingPointValues().create()
        }

        single {
            get<Retrofit>().create(AWApi::class.java)
        }
    }

    val weatherModule = module {
        single(named("WEATHER_HTTP_URL")) {
            BuildConfig.OPEN_WEATHER_API_URL.toHttpUrl()
        }

        single(named("WEATHER_HTTP_CACHE")) {
            val cacheSize = (20 * 1024 * 1024).toLong() // 20 MiB
            Cache(get<Context>().cacheDir, cacheSize)
        }

        single(named("WEATHER_HTTP_BUILDER")) {
            OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .cache(get(named("WEATHER_HTTP_CACHE")))
                .addNetworkInterceptor(CacheInterceptor())
                .build()
        }

        single(named("WEATHER_RETROFIT_BUILDER")) {
            Retrofit.Builder()
                .baseUrl(get<HttpUrl>(named("WEATHER_HTTP_URL")))
                .client(get<OkHttpClient>(named("WEATHER_HTTP_BUILDER")))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }

        single(named("WEATHER_HTTP_INIT")) {
            get<Retrofit>(named("WEATHER_RETROFIT_BUILDER")).create(AWWeatherApi::class.java)
        }
    }

    val repositoryModule = module {
        single {
            SectionRepository(get(), get(), get(named("WEATHER_HTTP_INIT")), get())
        }
        single {
            WebcamRepository(get(), get())
        }
    }
}