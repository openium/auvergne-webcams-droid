package fr.openium.auvergnewebcams.injection

import android.content.Context
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.grapesnberries.curllogger.CurlLoggerInterceptor
import com.squareup.picasso.Picasso
import fr.openium.auvergnewebcams.model.SectionList
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.MockApi
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.Calls
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * Created by Skyle on 19/02/2019.
 */
object DebugModules {
    const val mock = false

    val configModule = Kodein.Module("Config module") {

    }

    val serviceModule = Kodein.Module("Service module") {
        bind<Picasso>(overrides = true) with singleton {
            Picasso.Builder(instance()).loggingEnabled(true).build()
        }
    }

    val restModule = Kodein.Module("REST module") {

        bind<Interceptor>("CURL") with singleton {
            CurlLoggerInterceptor("CURL")
        }

        bind<OkHttpClient>(overrides = true) with provider {
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                .addInterceptor(instance("CURL"))
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
                networkBehaviour.setDelay(0, TimeUnit.MILLISECONDS)
                networkBehaviour.setFailurePercent(0)
                networkBehaviour.setVariancePercent(0)
                val apiMock = object : MockApi() {

                    override fun getSections(): Single<SectionList> {
                        val thisValue = instance<Context>().assets.open("aw-config.json")
                        val reader = InputStreamReader(thisValue)

                        val sObjectMapper = GsonBuilder()
                            .setExclusionStrategies(object : ExclusionStrategy {
                                override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                                    return false
                                }

                                override fun shouldSkipField(f: FieldAttributes): Boolean {
                                    return f.declaredClass == SectionList::class.java
                                }
                            })
                            .serializeNulls()
                            .create()

                        val listLoaded = sObjectMapper.fromJson(reader, SectionList::class.java) as SectionList

                        return delegate.returning(Calls.response(listLoaded)).getSections()
                    }
                }
                apiMock.delegate = MockRetrofit.Builder(
                    Retrofit.Builder()
                        .baseUrl(instance<HttpUrl>())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                )
                    .networkBehavior(networkBehaviour)
                    .build().create(AWApi::class.java)
                apiMock
            } else {
                instance<Retrofit>().create(AWApi::class.java)
            }
        }
    }
}