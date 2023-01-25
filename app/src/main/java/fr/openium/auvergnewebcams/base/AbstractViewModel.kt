package fr.openium.auvergnewebcams.base

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import fr.openium.auvergnewebcams.CustomApplication
import io.reactivex.disposables.CompositeDisposable

abstract class AbstractViewModel(application: Application) : AndroidViewModel(application) {

    protected var disposables: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    protected val context: Context
        get() = getApplication<CustomApplication>().applicationContext
}