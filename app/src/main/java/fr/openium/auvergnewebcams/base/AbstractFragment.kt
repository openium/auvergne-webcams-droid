package fr.openium.auvergnewebcams.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject

/**
 * Created by Openium on 19/02/2019.
 */

abstract class AbstractFragment : Fragment() {

    protected val disposables: CompositeDisposable = CompositeDisposable()

    protected val prefUtils by inject<PreferencesUtils>()
    protected val dateUtils by inject<DateUtils>()

    protected abstract val layoutId: Int

    // --- Life Cycle
    // ---------------------------------------------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
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
