package fr.openium.auvergnewebcams.rest

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.Calls

/**
 * Created by Openium on 19/02/2019.
 */
open class MockApi : AWApi {
    lateinit var delegate: BehaviorDelegate<AWApi>

    fun BehaviorDelegate<AWApi>.returningFail(code: Int): AWApi {
        val body = "Error".toResponseBody("txt".toMediaTypeOrNull())
        val response: Response<Any> = Response.error(code, body)
        return returning(Calls.response(response))
    }
}