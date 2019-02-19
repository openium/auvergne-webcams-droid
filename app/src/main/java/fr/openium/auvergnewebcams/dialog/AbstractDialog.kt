package fr.openium.auvergnewebcams.dialog

import androidx.appcompat.app.AppCompatDialogFragment
import io.reactivex.disposables.CompositeDisposable
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein

/**
 * Created by Skyle on 19/02/2019.
 */
abstract class AbstractDialog : AppCompatDialogFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()
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