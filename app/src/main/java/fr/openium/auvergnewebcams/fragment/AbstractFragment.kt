package fr.openium.auvergnewebcams.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm

abstract class AbstractFragment : Fragment() {
    protected abstract val layoutId: Int
    protected var oneTimeSubscriptions: CompositeDisposable = CompositeDisposable() //only subscribe one time and unsubscribe later
    protected var rebindSubscriptions: CompositeDisposable = CompositeDisposable() //Resubscribe in onstart

    protected var isAlive: Boolean = false
    protected var realm: Realm? = null

    open protected val customToolbarFragment: Toolbar? = null

    open protected val shoudWatchLeak: Boolean = true

    open val overrideTheme: Int = 0

    protected val kodeinInjector = KodeinInjector()

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onAttach(context: Context) {
        super.onAttach(context)
        realm = Realm.getDefaultInstance()
        isAlive = true
        kodeinInjector.inject(appKodein())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutId = layoutId
        val view: View = inflater.inflate(layoutId, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        startSubscription(rebindSubscriptions)
    }

    override fun onStop() {
        super.onStop()
        rebindSubscriptions.clear()
    }

    protected open fun startSubscription(onStartSubscriptions: CompositeDisposable) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        oneTimeSubscriptions.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
//        if (shoudWatchLeak) {
//            getApplicationBase()?.refWatcher?.watch(this)
//        }
        oneTimeSubscriptions.clear()
    }

    override fun onDetach() {
        super.onDetach()
        realm?.close()
        realm = null
        isAlive = false
    }
}
