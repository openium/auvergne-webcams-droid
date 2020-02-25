package fr.openium.auvergnewebcams.base

import androidx.appcompat.app.AppCompatDialogFragment
import com.bumptech.glide.Glide
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import io.reactivex.disposables.CompositeDisposable
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

/**
 * Created by Openium on 19/02/2019.
 */
abstract class AbstractDialog : AppCompatDialogFragment(), KodeinAware {

    override val kodein: Kodein by closestKodein()
    protected val prefUtils: PreferencesUtils by instance()
    protected val dateUtils: DateUtils by instance()
    protected val glide: Glide by instance()

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