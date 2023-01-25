package fr.openium.auvergnewebcams.ui.splash

import android.app.Application
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import fr.openium.auvergnewebcams.base.AbstractViewModel
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.rest.model.SectionList
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Completable
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit


class ViewModelSplash(app: Application) : AbstractViewModel(app), KoinComponent {

    private val sectionRepository by inject<SectionRepository>()

    companion object {
        const val MINIMUM_SECONDS_TO_WAIT = 2L
    }

    // Update all the data the app needs
    fun updateData(): Completable =
        Completable.timer(MINIMUM_SECONDS_TO_WAIT, TimeUnit.SECONDS).fromIOToMain().mergeWith(
            if (context.hasNetwork) {
                sectionRepository.fetch().doOnSuccess {
                    Timber.d("Loading from network")
                }.doOnError {
                    Timber.e(it)
                    loadFromJson()
                }.ignoreElement()
            } else {
                // If we don't have internet connection
                Completable.fromCallable {
                    loadFromJson()
                }
            }
        ).fromIOToMain()

    // If there is no access to the online content, just load the local one
    private fun loadFromJson() {
        Timber.d("Loading local.json")

        // Get sections from DB
        val sections = sectionRepository.getSections()

        if (sections.isEmpty()) {
            getSectionsFromAssets()?.also {
                sectionRepository.insertSectionsAndWebcams(it)
            }
        }
    }

    // The function that load data from .json
    private fun getSectionsFromAssets(): SectionList? {
        val inputStream = context.assets.open("aw-config.json")
        val gson = GsonBuilder().create()
        val jsonReader = JsonParser().parse(InputStreamReader(inputStream))
        return gson.fromJson(jsonReader, SectionList::class.java)
    }
}