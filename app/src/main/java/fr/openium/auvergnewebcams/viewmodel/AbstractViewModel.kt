package fr.openium.auvergnewebcams.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import fr.openium.auvergnewebcams.CustomApplication
import fr.openium.auvergnewebcams.rest.ApiHelper
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

abstract class AbstractViewModel(application: Application) : AndroidViewModel(application), KodeinAware {

    protected var disposables: CompositeDisposable = CompositeDisposable()

    protected val realm: Realm = Realm.getDefaultInstance()

    override val kodein: Kodein by closestKodein(context)

    protected val apiHelper: ApiHelper by instance()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        realm.close()
    }

    protected val context: Context
        get() = getApplication<CustomApplication>().applicationContext
}