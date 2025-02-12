package fr.openium.auvergnewebcams.event

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ForegroundBackgroundListener(
    val context: Context,
) : DefaultLifecycleObserver, KoinComponent {

    private val prefUtils by inject<PreferencesUtils>()

    private val disposables: CompositeDisposable = CompositeDisposable()

    private var refreshTimer: Disposable? = null

    private var networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            eventHasNetwork.accept(context.hasNetwork)
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        // Init once
        setTimer()

        eventRefreshDelayValueChanged.subscribe({
            setTimer()
        }, { Timber.e(it) }).addTo(disposables)

        // Register to network connectivity changes
        context.registerReceiver(
            networkReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        // Unregister to network connectivity changes
        context.unregisterReceiver(networkReceiver)

        disposables.clear()
    }

    private fun setTimer() {
        refreshTimer?.dispose()
        refreshTimer = null

        refreshTimer =
            Observable.timer(prefUtils.webcamsDelayRefreshValue.toLong(), TimeUnit.MINUTES)
                .subscribe({
                    setTimer()
                }, { Timber.e(it) }).addTo(disposables)
    }
}