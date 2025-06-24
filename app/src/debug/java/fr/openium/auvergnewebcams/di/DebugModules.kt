package fr.openium.auvergnewebcams.di

import fr.openium.auvergnewebcams.model.AWClient
import fr.openium.auvergnewebcams.rest.AWApi
import fr.openium.auvergnewebcams.rest.MockApi
import io.reactivex.schedulers.Schedulers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.util.concurrent.TimeUnit

/**
 * Created by Openium on 19/02/2019.
 */
object DebugModules {
    const val mock = true

    val databaseService = module {
        single {
            AWClient.getInstance(get())
        }
    }

    val restModule = module {
        single {
            OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }).cache(get()).build()
        }

        single {
            Retrofit.Builder()
                .baseUrl(get<HttpUrl>()).client(get())
                .addConverterFactory(GsonConverterFactory.create(get()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
        }

        single {
            if (mock) {
                val behaviour = NetworkBehavior.create().apply {
                    setDelay(0, TimeUnit.MILLISECONDS)
                    setFailurePercent(0)
                    setVariancePercent(0)
                }

                val retrofit = get<Retrofit>()

                val mockRetrofit = MockRetrofit.Builder(retrofit)
                    .networkBehavior(behaviour)
                    .build()

                val delegate = mockRetrofit.create(AWApi::class.java)

                MockApi(delegate, get())
            } else {
                get<Retrofit>().create(AWApi::class.java)
            }
        }
    }
}