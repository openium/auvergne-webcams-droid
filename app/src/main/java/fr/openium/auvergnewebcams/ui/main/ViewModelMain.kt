package fr.openium.auvergnewebcams.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Completable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.concurrent.TimeUnit


class ViewModelMain(app: Application) : AbstractViewModel(app), KoinComponent {

    private val sectionRepository by inject<SectionRepository>()

    val isRefreshing = MutableLiveData<Boolean>()

    val sections by lazy {
        sectionRepository.watchSectionsWithCameras()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())
    }

    // Update all the data the app needs
    fun updateData(): Completable =
        Completable.timer(MINIMUM_SECONDS_TO_WAIT, TimeUnit.SECONDS)
            .mergeWith(sectionRepository.fetch()
                .doOnSuccess {
                    Timber.d("Loading from network: OK")
                }.doOnError {
                    Timber.e(it, "Loading from network: KO")
                }.ignoreElement()
            ).fromIOToMain()

    fun setRefreshing(refresh: Boolean) {
        isRefreshing.postValue(refresh)
    }

    companion object {
        const val MINIMUM_SECONDS_TO_WAIT = 2L
    }
}