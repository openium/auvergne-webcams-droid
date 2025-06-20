package fr.openium.auvergnewebcams.rest

import android.content.Context
import com.google.gson.Gson
import fr.openium.auvergnewebcams.rest.model.SectionList
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import retrofit2.mock.BehaviorDelegate

/**
 * Created by Openium on 19/02/2019.
 */
open class MockApi(
    private val delegate: BehaviorDelegate<AWApi>,
    private val context: Context
) : AWApi {

    override suspend fun getSections(): Response<SectionList> {
        val json = context.assets.open("aw-config.json")
            .bufferedReader()
            .use { it.readText() }

        val sectionList: SectionList =
            Gson().fromJson(json, SectionList::class.java)

        return delegate
            .returningResponse(sectionList)
            .getSections()
    }

    fun BehaviorDelegate<AWApi>.returningFail(code: Int): AWApi {
        val errorBody = "Error".toResponseBody("text/plain".toMediaTypeOrNull())
        val errorResp = Response.error<SectionList>(code, errorBody)
        return returningResponse(errorResp)
    }

}
