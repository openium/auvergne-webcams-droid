package fr.openium.auvergnewebcams.utils

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.crashlytics.android.answers.SearchEvent


/**
 * Created by Openium on 19/02/2019.
 */
object FabricUtils {

    /**
     * Generic method to log all ContentViewEvent
     */
    fun logContentViewEvent(analytics: Answers, contentName: String, contentType: String? = null, itemSelected: String? = null) {
        val contentView = ContentViewEvent().putContentName(contentName)

        contentType?.let {
            contentView.putContentType(it)
        }

        itemSelected?.let {
            contentView.putContentId(itemSelected)
        }

        analytics.logContentView(contentView)
    }

    /**
     * Generic method to log all SearchEvent
     */
    fun logSearchEvent(analytics: Answers, searchText: String) {
        analytics.logSearch(SearchEvent().putQuery(searchText))
    }
}