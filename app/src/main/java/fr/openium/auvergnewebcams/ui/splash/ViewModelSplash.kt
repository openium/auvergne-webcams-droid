package fr.openium.auvergnewebcams.ui.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import fr.openium.auvergnewebcams.event.eventHasNetwork
import fr.openium.auvergnewebcams.repository.SectionRepository
import fr.openium.auvergnewebcams.rest.model.SectionList
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Completable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class ViewModelSplash : ViewModel(), KoinComponent {

    private val sectionRepository by inject<SectionRepository>()

    companion object {
        const val MINIMUM_SECONDS_TO_WAIT = 2L
    }

    // Update all the data the app needs
    fun updateData(context: Context): Completable =
        Completable.timer(MINIMUM_SECONDS_TO_WAIT, TimeUnit.SECONDS).fromIOToMain().mergeWith(
            if (eventHasNetwork.value == true) {
                sectionRepository.fetch().doOnSuccess {
                    Timber.d("Loading from network")
                }.doOnError {
                    Timber.e(it)
                    loadFromJson(context)
                }.ignoreElement()
            } else {
                // If we don't have internet connection
                Completable.fromCallable {
                    loadFromJson(context)
                }
            }
        ).fromIOToMain()

    // If there is no access to the online content, just load the local one
    private fun loadFromJson(context: Context) {
        Timber.d("Loading local.json")

        // Get sections from DB
        val sections = sectionRepository.getSections()

        if (sections.isEmpty()) {
            getSectionsFromAssets(context)?.also {
                sectionRepository.insertSectionsAndWebcams(it)
            }
        } else {
            sectionRepository.updateSectionsWeather(sections)
        }
    }

    // The function that load data from .json
    private fun getSectionsFromAssets(context: Context): SectionList? {
        val inputStream = context.assets.open("aw-config.json")
        val gson = GsonBuilder().create()
        val jsonReader = JsonParser().parse(InputStreamReader(inputStream))
        return gson.fromJson(jsonReader, SectionList::class.java)
    }

}