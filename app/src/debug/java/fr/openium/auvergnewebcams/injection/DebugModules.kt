package fr.openium.auvergnewebcams.injection

import android.content.Context
import com.github.salomonbrys.kodein.*
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.SectionList
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.MockApi
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.Result
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * Created by t.coulange on 21/03/2017.
 */
object DebugModules {
    val mock = false

    val configModule = Kodein.Module {
        bind<RefWatcher>(overrides = true) with singleton { LeakCanary.install(instance()) }
    }

    val serviceModule = Kodein.Module {
        //        bind<GlideRequests>(overrides = true) with singleton { GlideApp.with(instance<Context>()) }
    }

    val restModule = Kodein.Module {

        bind<Cache>(overrides = true) with provider {
            val cacheSize = (20 * 1024 * 1024).toLong() // 20 MiB
            Cache(instance<Context>().cacheDir, cacheSize)
        }

        bind<HttpUrl>(overrides = true) with singleton {
            HttpUrl.parse(instance<Context>().getString(R.string.url_prod))!!
        }

        bind<OkHttpClient>(overrides = true) with provider {
            OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .cache(instance())
                    .build()
        }

        bind<Retrofit>(overrides = true) with singleton {
            Retrofit.Builder()
                    .baseUrl(instance<HttpUrl>()).client(instance())
                    .addConverterFactory(GsonConverterFactory.create(instance()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .build()
        }

        bind<AWApi>(overrides = true) with provider {
            if (mock) {
                val networkBehaviour = NetworkBehavior.create()
                networkBehaviour.setDelay(250, TimeUnit.MILLISECONDS)
                networkBehaviour.setFailurePercent(0)
                networkBehaviour.setVariancePercent(0)
                val apiMock = object : MockApi() {

                    override fun getSections(): Single<Result<SectionList>> {
                        val thisValue = instance<Context>().getAssets().open("aw-config.json")
                        val reader = InputStreamReader(thisValue)

                        val sObjectMapper = GsonBuilder()
                                .setExclusionStrategies(object : ExclusionStrategy {
                                    override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                                        return false
                                    }

                                    override fun shouldSkipField(f: FieldAttributes): Boolean {
                                        return f.getDeclaredClass().equals(SectionList::class.java)
                                    }
                                })
                                .serializeNulls()
                                .create()

                        val listLoaded = sObjectMapper.fromJson(reader, SectionList::class.java) as SectionList

                        return delegate.returningResponse(listLoaded).getSections()
                    }
                }
                apiMock.delegate = MockRetrofit.Builder(Retrofit.Builder()
                        .baseUrl(instance<HttpUrl>())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build())
                        .networkBehavior(networkBehaviour)
                        .build().create(AWApi::class.java)
                apiMock
            } else {
                instance<Retrofit>().create(AWApi::class.java)
            }
        }
    }
}