package fr.openium.auvergnewebcams.repository

import fr.openium.auvergnewebcams.model.CustomClient
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.rest.ApiHelper
import fr.openium.auvergnewebcams.rest.model.SectionList
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by Openium on 19/02/2019.
 */
class SectionRepository(
    private val client: CustomClient,
    private val apiHelper: ApiHelper,
    private val webcamRepository: WebcamRepository
) {

    // WS

    fun fetch(): Single<SectionList> {
        return apiHelper.getSections(this, webcamRepository)
    }

    // Local

    fun getSections(): List<Section> {
        return client.database.sectionDao().getSections()
    }

    fun getSectionsObs(): Observable<List<Section>> {
        return client.database.sectionDao().getSectionsObs()
    }

    fun update(section: Section): Int {
        return client.database.sectionDao().update(section)
    }

    fun update(sections: List<Section>): Int {
        return client.database.sectionDao().update(sections)
    }

    fun insert(section: Section): Long {
        return client.database.sectionDao().insert(section)
    }

    fun insert(sections: List<Section>): List<Long> {
        return client.database.sectionDao().insert(sections)
    }

    fun delete(section: Section) {
        client.database.sectionDao().delete(section)
    }

    fun delete(sections: List<Section>) {
        client.database.sectionDao().delete(sections)
    }

    fun deleteAllNotInUIDs(ids: List<Long>): Completable {
        return client.database.sectionDao().deleteAllNotInUids(ids)
    }
}