package fr.openium.auvergnewebcams.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.crashlytics.android.answers.Answers
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.android.appKodein
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm

abstract class AbstractFragment : Fragment() {
    protected abstract val layoutId: Int

    protected var oneTimeDisposables: CompositeDisposable = CompositeDisposable() //only subscribe one time and unsubscribe later

    protected var isAlive: Boolean = false
    protected var realm: Realm? = null

    protected var mFirebaseAnalytics: FirebaseAnalytics? = null
    protected var mAnswersAnalytics: Answers? = null

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
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
        mAnswersAnalytics = Answers.getInstance()
        kodeinInjector.inject(appKodein())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutId = layoutId
        val view: View = inflater.inflate(layoutId, container, false)
        return view
    }

    protected open fun startSubscription(onStartSubscriptions: CompositeDisposable) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        oneTimeDisposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
//        if (shoudWatchLeak) {
//            getApplicationBase()?.refWatcher?.watch(this)
//        }
        oneTimeDisposables.clear()
    }

    override fun onDetach() {
        super.onDetach()
        realm?.close()
        realm = null
        isAlive = false
    }
}
