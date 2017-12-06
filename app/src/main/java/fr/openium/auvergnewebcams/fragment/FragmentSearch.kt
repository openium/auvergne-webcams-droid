package fr.openium.auvergnewebcams.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.jakewharton.rxbinding2.widget.RxSearchView
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.adapter.AdapterSearch
import fr.openium.auvergnewebcams.ext.applicationContext
import fr.openium.auvergnewebcams.ext.gone
import fr.openium.auvergnewebcams.ext.show
import fr.openium.auvergnewebcams.model.Webcam
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by laura on 05/12/2017.
 */
class FragmentSearch : AbstractFragment() {

    private val webcams = ArrayList<Webcam>()

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        textViewSearch.setIconifiedByDefault(false)
        textViewSearch.requestFocus()

        oneTimeSubscriptions.add(RxSearchView.queryTextChangeEvents(textViewSearch)
                .skip(1)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    initSearchAdapter(it.queryText().toString())
                })

        val webcamsBdd = realm!!.where(Webcam::class.java)
                .findAllSorted(Webcam::order.name)
        if (webcamsBdd != null) {
            webcams.addAll(realm!!.copyFromRealm(webcamsBdd))
        }
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun initSearchAdapter(search: String) {
        val webcamsAdapter = ArrayList<Webcam>()
        if (!search.isEmpty()) {
            val webcamsSearch = webcams.filter {
                it.title?.toLowerCase(Locale.getDefault())?.contains(search.toLowerCase(Locale.getDefault())) ?: false
                        || it.tags?.filter { it.toLowerCase(Locale.getDefault()).contains(search.toLowerCase(Locale.getDefault())) }?.isNotEmpty() ?: false
            }
            webcamsAdapter.addAll(webcamsSearch)
        }

        if (recyclerViewSearch.adapter == null) {
            recyclerViewSearch.layoutManager = LinearLayoutManager(applicationContext)
            recyclerViewSearch.adapter = AdapterSearch(applicationContext, webcamsAdapter, { webcam ->
                val intent: Intent = Intent(context, ActivityWebcam::class.java).apply {
                    putExtra(Constants.KEY_ID, webcam.uid)
                    putExtra(Constants.KEY_TYPE, webcam.type)
                }
                val bundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext, android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle()
                startActivity(intent, bundle)
            })
        } else {
            (recyclerViewSearch.adapter as AdapterSearch).items = webcamsAdapter
            recyclerViewSearch.adapter.notifyDataSetChanged()
        }

        if (webcamsAdapter.isEmpty()) {
            textViewResultSearch.gone()
        } else {
            textViewResultSearch.show()
            val nbResult = getString(R.string.search_result, webcamsAdapter.size)
            val spannable = SpannableString(String.format("%s %s", nbResult, search))
            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.white)), 0, nbResult.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.blue)), nbResult.length, nbResult.length + search.length + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            textViewResultSearch.setText(spannable)
        }
    }

    // =================================================================================================================
    // Overridden
    // =================================================================================================================

    override val layoutId: Int
        get() = R.layout.fragment_search

}