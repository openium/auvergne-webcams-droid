package fr.openium.auvergnewebcams.base

import androidx.appcompat.app.AppCompatDialogFragment
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Openium on 19/02/2019.
 */
abstract class AbstractDialog : AppCompatDialogFragment() {

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