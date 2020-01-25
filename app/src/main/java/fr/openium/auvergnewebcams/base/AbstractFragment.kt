package fr.openium.auvergnewebcams.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.disposables.CompositeDisposable
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

/**
 * Created by Openium on 19/02/2019.
 */

abstract class AbstractFragment : Fragment(), KodeinAware {

    protected val disposables: CompositeDisposable = CompositeDisposable()
    protected val rebindDisposables: CompositeDisposable = CompositeDisposable() //Resubscribe in onstart

    protected val glide: Glide by instance()

    override val kodein: Kodein by closestKodein()
    protected val preferencesUtils: PreferencesUtils by instance()

    protected abstract val layoutId: Int

    // --- Life Cycle
    // ---------------------------------------------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onStart() {
        super.onStart()

        startSubscription(rebindDisposables)
    }

    override fun onStop() {
        super.onStop()
        rebindDisposables.clear()
    }

    protected open fun startSubscription(onStartDisposables: CompositeDisposable) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
