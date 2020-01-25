package fr.openium.auvergnewebcams.base

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import fr.openium.auvergnewebcams.CustomApplication
import io.reactivex.disposables.CompositeDisposable
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

abstract class AbstractViewModel(application: Application) : AndroidViewModel(application), KodeinAware {

    protected var disposables: CompositeDisposable = CompositeDisposable()

    override val kodein: Kodein by closestKodein(context)

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    protected val context: Context
        get() = getApplication<CustomApplication>().applicationContext
}