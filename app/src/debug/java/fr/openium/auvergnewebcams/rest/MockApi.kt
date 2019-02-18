package fr.openium.auvergnewebcams.rest

import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.Calls

/**
 * Created by Openium on 20/03/2018.
 */
open class MockApi : AWApi {
    lateinit var delegate: BehaviorDelegate<AWApi>

    fun BehaviorDelegate<AWApi>.returningFail(code: Int): AWApi {
        val body = ResponseBody.create(MediaType.parse("txt"), "Error")
        val response: Response<Any> = Response.error(code, body)
        return returning(Calls.response(response))
    }
}