package fr.openium.auvergnewebcams.viewmodel

import android.app.Application
import fr.openium.auvergnewebcams.model.Section
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit


class ViewModelMain(app: Application) : AbstractViewModel(app) {

    companion object {
        const val MINIMUM_SECONDS_TO_WAIT = 2L
    }

    //Update all the data the app needs
    fun updateData(): Completable {
        return Completable.timer(MINIMUM_SECONDS_TO_WAIT, TimeUnit.SECONDS).mergeWith(
            apiHelper.getSections().doOnSuccess {
                Timber.d("Loading from network: OK")
            }.doOnError {
                Timber.e("Loading from network: KO")
            }.ignoreElement()
        ).observeOn(AndroidSchedulers.mainThread())
    }

    fun getSections(): List<Section> {
        return realm.copyFromRealm(realm.where(Section::class.java).findAll())
    }
}