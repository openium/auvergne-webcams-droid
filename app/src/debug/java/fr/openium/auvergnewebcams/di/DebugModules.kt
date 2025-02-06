package fr.openium.auvergnewebcams.di

import android.content.Context
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import fr.openium.auvergnewebcams.model.AWClient
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.MockApi
import fr.openium.auvergnewebcams.rest.model.SectionList
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.Calls
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * Created by Openium on 19/02/2019.
 */
object DebugModules {
    const val mock = true

    val databaseService = module {
        single() {
            AWClient.getInstance(get())
        }
    }

    val restModule = module {
        single() {
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .cache(get())
                .build()
        }

        single() {
            Retrofit.Builder()
                .baseUrl(get<HttpUrl>()).client(get())
                .addConverterFactory(GsonConverterFactory.create(get()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
        }

        single() {
            if (mock) {
                val networkBehaviour = NetworkBehavior.create()
                networkBehaviour.setDelay(0, TimeUnit.MILLISECONDS)
                networkBehaviour.setFailurePercent(0)
                networkBehaviour.setVariancePercent(0)
                val apiMock = object : MockApi() {

                    override fun getSections(): Single<SectionList> {
                        val thisValue = get<Context>().assets.open("aw-config.json")
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

                        val listLoaded =
                            sObjectMapper.fromJson(reader, SectionList::class.java) as SectionList

                        return delegate.returning(Calls.response(listLoaded)).getSections()
                    }
                }

                apiMock.delegate = MockRetrofit.Builder(
                    Retrofit.Builder()
                        .baseUrl(get<HttpUrl>())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                ).networkBehavior(networkBehaviour).build().create(AWApi::class.java)
                apiMock
            } else {
                get<Retrofit>().create(AWApi::class.java)
            }
        }
    }
}