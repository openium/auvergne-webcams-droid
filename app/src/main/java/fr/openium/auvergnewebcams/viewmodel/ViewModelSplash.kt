package fr.openium.auvergnewebcams.viewmodel

import android.app.Application
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import fr.openium.auvergnewebcams.ext.hasNetwork
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.SectionList
import fr.openium.rxtools.ext.fromIOToMain
import io.reactivex.Completable
import io.realm.Realm
import timber.log.Timber
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit


class ViewModelSplash(app: Application) : AbstractViewModel(app) {

    companion object {
        const val MINIMUM_SECONDS_TO_WAIT = 2L
    }

    //Update all the data the app needs
    fun updateData(): Completable {
        return Completable.timer(MINIMUM_SECONDS_TO_WAIT, TimeUnit.SECONDS).fromIOToMain().mergeWith(
            if (context.hasNetwork) {
                apiHelper.getSections().doOnSuccess {
                    Timber.d("Loading from network")
                }.doOnError {
                    loadFromJson(realm)
                }.ignoreElement()
            } else {
                //If we don't have internet connection
                Completable.fromCallable {
                    Realm.getDefaultInstance().use { realm -> loadFromJson(realm) }
                }
            }
        ).fromIOToMain()
    }

    //If there is no access to the online content, just load the local one
    private fun loadFromJson(realm: Realm) {
        Timber.d("Loading local.json")

        //Check if there is no Section in Realm before loading these
        val isRealmSectionEmpty = realm.where(Section::class.java).findAll().count() == 0

        if (isRealmSectionEmpty) {
            val sections = getSectionsFromAssets()
            if (sections != null) {
                realm.executeTransaction {
                    it.insertOrUpdate(sections.sections)
                }
            }
        }
    }

    //The function that load data from .json
    private fun getSectionsFromAssets(): SectionList? {
        val inputStream = context.assets?.open("aw-config.json")
        val gson = GsonBuilder().create()
        val jsonReader = JsonParser().parse(InputStreamReader(inputStream))
        return gson.fromJson(jsonReader, SectionList::class.java)
    }
}