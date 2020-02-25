package fr.openium.auvergnewebcams.repository

import fr.openium.auvergnewebcams.model.AWClient
import fr.openium.auvergnewebcams.model.entity.Section
import fr.openium.auvergnewebcams.rest.ApiHelper
import fr.openium.auvergnewebcams.rest.model.SectionList
import fr.openium.auvergnewebcams.utils.Optional
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by Openium on 19/02/2019.
 */
class SectionRepository(private val client: AWClient, private val apiHelper: ApiHelper, private val webcamRepository: WebcamRepository) {

    // WS

    fun fetch(): Single<SectionList> = apiHelper.getSections(this, webcamRepository)

    // Local

    fun getSectionSingle(sectionId: Long): Single<Optional<Section>> =
        client.database.sectionDao().getSectionSingle(sectionId).map {
            Optional.of(it)
        }

    fun getSections(): List<Section> =
        client.database.sectionDao().getSections()

    fun getSectionsSingle(): Single<List<Section>> =
        client.database.sectionDao().getSectionsSingle()

    fun update(section: Section): Int =
        client.database.sectionDao().update(section)

    fun update(sections: List<Section>): Int =
        client.database.sectionDao().update(sections)

    fun insert(section: Section): Long =
        client.database.sectionDao().insert(section)

    fun insert(sections: List<Section>): List<Long> =
        client.database.sectionDao().insert(sections)

    fun deleteAllNotInUIDs(ids: List<Long>): Completable =
        client.database.sectionDao().deleteAllNotInUids(ids)
}