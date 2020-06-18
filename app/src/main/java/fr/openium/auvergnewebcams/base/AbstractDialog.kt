package fr.openium.auvergnewebcams.base

import androidx.appcompat.app.AppCompatDialogFragment
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject

/**
 * Created by Openium on 19/02/2019.
 */
abstract class AbstractDialog : AppCompatDialogFragment() {

    protected val prefUtils by inject<PreferencesUtils>()
    protected val dateUtils by inject<DateUtils>()

    protected val disposables: CompositeDisposable = CompositeDisposable()

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}