package fr.openium.auvergnewebcams.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPropertyAnimatorCompat
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm

abstract class AbstractFragment : Fragment() {

    protected var oneTimeSubscriptions: CompositeDisposable = CompositeDisposable() //only subscribe one time and unsubscribe later
    protected var rebindSubscriptions: CompositeDisposable = CompositeDisposable() //Resubscribe in onstart

    protected var isAlive: Boolean = false
    protected val mRealm: Realm = Realm.getDefaultInstance()

    open protected val customToolbarFragment: Toolbar? = null

    open protected val shoudWatchLeak: Boolean = true

    open val overrideTheme: Int = 0

    protected val kodeinInjector = KodeinInjector()

    protected val wrappedContext: Context by lazy {
        if (overrideTheme == 0) {
            context
        } else {
            ContextThemeWrapper(activity, overrideTheme)
        }
    }

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAlive = true
        kodeinInjector.inject(appKodein())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var layoutInflater = inflater
        if (overrideTheme != 0) {
            layoutInflater = inflater.cloneInContext(wrappedContext)
        }

        val layoutId = layoutId
        val view: View = layoutInflater.inflate(layoutId, container, false)
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
        isAlive = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    protected abstract val layoutId: Int

    inline fun ViewPropertyAnimatorCompat.withEndActionSafe(crossinline body: () -> Unit): ViewPropertyAnimatorCompat {
        return withEndAction {
            if (view != null) {
                body()
            }
        }
    }

    inline fun ViewPropertyAnimatorCompat.withStartActionSafe(crossinline body: () -> Unit): ViewPropertyAnimatorCompat {
        return withStartAction {
            if (view != null) {
                body()
            }
        }
    }
}
