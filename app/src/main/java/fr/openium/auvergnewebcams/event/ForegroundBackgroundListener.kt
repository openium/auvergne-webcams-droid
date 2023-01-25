package fr.openium.auvergnewebcams.event

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ForegroundBackgroundListener(val context: Context) : LifecycleObserver, KoinComponent {

    private val prefUtils by inject<PreferencesUtils>()

    private val disposables: CompositeDisposable = CompositeDisposable()

    private var refreshTimer: Disposable? = null

    private var networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            eventHasNetwork.accept(context.hasNetwork)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Timber.d("onCreate")

        // Init once
        setTimer()

        eventRefreshDelayValueChanged.subscribe({
            setTimer()
        }, { Timber.e(it, "Error listening to refresh value change in background") }).addTo(disposables)

        // Register to network connectivity changes
        context.registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Timber.d("onDestroy")

        // Unregister to network connectivity changes
        context.unregisterReceiver(networkReceiver)

        disposables.clear()
    }

    private fun setTimer() {
        refreshTimer?.dispose()
        refreshTimer = null

        refreshTimer = Observable.timer(prefUtils.webcamsDelayRefreshValue.toLong(), TimeUnit.MINUTES).subscribe({
            removeGlideCache()

            setTimer()
        }, { Timber.e(it, "Error refresh timer") }).addTo(disposables)
    }

    private fun removeGlideCache() {
        // TODO
//        Observable
//            .fromCallable {
//                Glide.get(context).clearDiskCache()
//            }
//            .fromIOToMain()
//            .subscribe({
//                Glide.get(context).clearMemory()
//            }, { Timber.e(it, "Error cleaning Glide cache") }).addTo(disposables)
    }
}