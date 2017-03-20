package fr.openium.auvergnewebcams.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.model.SectionList
import io.realm.Realm

/**
 * Created by laura on 20/03/2017.
 */
class ActivitySplash : AbstractActivity() {


    companion object {
        private val END_SPLASH_TIME = 1000
    }

    private var mIsAlive: Boolean = false
    private var mNbCurrentTask: Int = 0

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIsAlive = true
        setContentView(R.layout.activity_splash)

        mNbCurrentTask += 2
        AsyncTaskLoadDatas().execute()
        startHandlerEndSplash()
    }

    override fun onDestroy() {
        super.onDestroy()
        mIsAlive = false
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun startHandlerEndSplash() {
        Handler().postDelayed({
            mNbCurrentTask--
            startActivityMain()
        }, END_SPLASH_TIME.toLong())
    }

    private fun startActivityMain() {
        if (mIsAlive && mNbCurrentTask == 0) {
            val intent = Intent(this, ActivityMain::class.java)
            startActivity(intent)
            finish()
        }
    }

    // =================================================================================================================
    // Async task
    // =================================================================================================================

    private inner class AsyncTaskLoadDatas : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg arg: Void?): Void? {
            val realm = Realm.getDefaultInstance()

            val sections = SectionList.getSectionsFromAssets(applicationContext)
            if(sections != null) {
                realm.executeTransaction { realm ->
                    realm.insertOrUpdate(sections.sections)
                }
            }

            realm.close()
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            mNbCurrentTask --
            startActivityMain()
        }
    }
}