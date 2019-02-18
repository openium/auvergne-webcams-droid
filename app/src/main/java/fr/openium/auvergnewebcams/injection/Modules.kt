package fr.openium.auvergnewebcams.injection

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.ApiHelper
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.kodein.di.Kodein
import org.kodein.di.generic.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by Openium on 20/03/2018.
 */

object Modules {

    val configModule = Kodein.Module("Config Module") {
        constant("mock") with false
    }

    val serviceModule = Kodein.Module("Service Module") {
        bind<Picasso>() with singleton {
            Picasso.Builder(instance()).build()
        }
    }

    val restModule = Kodein.Module("REST Module") {
        bind<Cache>() with provider {
            val cacheSize = (20 * 1024 * 1024).toLong() // 20 MiB
            Cache(instance<Context>().cacheDir, cacheSize)
        }

        bind<HttpUrl>() with singleton {
            HttpUrl.parse(instance<Context>().getString(R.string.url_dev))!!
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

    val weatherModule = Kodein.Module("Weather module") {

        bind<HttpUrl>("weather") with singleton {
            HttpUrl.parse(instance<Context>().getString(R.string.url_weather))!!
        }

        bind<Retrofit>("weather") with singleton {
            Retrofit.Builder()
                .baseUrl(instance<HttpUrl>("weather")).client(instance())
                .addConverterFactory(GsonConverterFactory.create(instance()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
        }

//        bind<AWWeatherApi>() with singleton {
//            instance<Retrofit>("weather").create(AWWeatherApi::class.java)
//        }
    }
}
