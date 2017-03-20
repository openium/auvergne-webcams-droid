package fr.openium.auvergnewebcams.model

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import io.realm.RealmList
import java.io.InputStreamReader

/**
 * Created by laura on 20/03/2017.
 */
data class SectionList(val sections: RealmList<Section>) {

    companion object {

        fun getSectionsFromAssets(context: Context) : SectionList? {
            var sections: SectionList? = null
            val inputStream = context.assets?.open("aw-config.json")
            val gson = GsonBuilder().create()
            val jsonReader = JsonParser().parse(InputStreamReader(inputStream))
            sections = gson.fromJson(jsonReader, SectionList::class.java)
            return sections
        }

    }

}