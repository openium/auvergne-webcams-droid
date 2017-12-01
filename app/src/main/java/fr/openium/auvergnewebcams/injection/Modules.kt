package fr.openium.auvergnewebcams.injection

import android.content.Context
import com.github.salomonbrys.kodein.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.leakcanary.RefWatcher
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.rest.AWApi
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by t.coulange on 07/12/2016.
 */
object Modules {

    val configModule = Kodein.Module {
        bind<RefWatcher>() with singleton { RefWatcher.DISABLED }
    }

    val serviceModule = Kodein.Module {
        //        bind<Picasso>() with singleton { Picasso.Builder(instance()).build() }
        bind<Gson>() with singleton {
            GsonBuilder().create()
        }

    }

    val restModule = Kodein.Module {

        bind<Cache>() with provider {
            val cacheSize = (20 * 1024 * 1024).toLong() // 20 MiB
            Cache(instance<Context>().cacheDir, cacheSize)
        }

        bind<HttpUrl>() with singleton {
            HttpUrl.parse(instance<Context>().getString(R.string.url_prod))!!
        }

        bind<OkHttpClient>() with provider {
            OkHttpClient.Builder().cache(instance()).build()
        }

        bind<Retrofit>() with singleton {
            Retrofit.Builder()
                    .baseUrl(instance<HttpUrl>()).client(instance())
                    .addConverterFactory(GsonConverterFactory.create(instance()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build()
        }


        bind<AWApi>() with singleton {
            instance<Retrofit>().create(AWApi::class.java)
        }
    }
}