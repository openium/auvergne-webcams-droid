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
import fr.openium.auvergnewebcams.custom.LastUpdateDateInterceptor
import fr.openium.auvergnewebcams.event.ForegroundBackgroundListener
import fr.openium.auvergnewebcams.model.AWClient
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import okhttp3.Cache
import okhttp3.HttpUrl
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
                .okHttpClient(get<OkHttpClient>(named("COIL")))
                .crossfade(true)
                .allowRgb565(true)
                .logger(DebugLogger(Log.VERBOSE))
                .components {
                    add(VideoFrameDecoder.Factory())
                }.build()
        }

        single(named("COIL")) {
            OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .addInterceptor(get<LastUpdateDateInterceptor>(named("COIL")))
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
        }

        single(named("COIL")) {
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

    val repositoryModule = module {
        single {
            SectionRepository(get(), get(), get())
        }
        single {
            WebcamRepository(get(), get())
        }
    }
}
