package fr.openium.auvergnewebcams.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.model.adapter.ItemWebcam
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_list_camera.*



/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentListCamera : AbstractFragment(), OnWebcamClickListener {


    companion object {
        @JvmStatic val TAG = FragmentListCamera::class.java.simpleName
    }

    protected val mItems = ArrayList<ItemWebcam>()

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mRealm.executeTransactionAsync (
                { realm ->
                    val sections = realm.where(Section::class.java)
                            .findAllSorted(Section::order.name, Sort.ASCENDING)
                    for(section in sections) {
                        for(webcam in section.webcams) {
                            val nameSection = if(section.title == null) "" else section.title!!
                            val item = ItemWebcam(realm.copyFromRealm(webcam), nameSection)
                            mItems.add(item)
                        }
                    }

                },
                {
                    initAdapter()
                },
                {
                    error ->
                    Log.e(TAG, error.message)
                    initAdapter()
                })


    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun initAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AdapterWebcam(activity, this, mItems)
    }

    // =================================================================================================================
    // Overridden methods
    // =================================================================================================================

    override val layoutId: Int
        get() = R.layout.fragment_list_camera



    override fun onWebcamClick(webcam: Webcam, position: Int) {
        val intent = Intent(context, ActivityWebcam::class.java)
        intent.putExtra(Constants.KEY_ID, webcam.uid)
        startActivity(intent)
    }

    class AdapterWebcam(val context: Activity, val listener: OnWebcamClickListener? = null, val items: List<ItemWebcam>) : RecyclerView.Adapter<AdapterWebcam.WebcamHolder>() {

        val heightImage: Int
        val widthImage: Int

        init {
            heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
            val display = context.windowManager.getDefaultDisplay()
            val size = Point()
            display.getSize(size)
            widthImage = size.x
        }

        override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
            val item = items.get(position)

            val section = item.nameSection
            val hasHeader: Boolean
            if(position == 0) {
                hasHeader = true
            } else {
                val prevItem = items.get(position - 1)
                if(section == prevItem.nameSection) {
                    hasHeader = false
                } else {
                    hasHeader = true
                }
            }

            val webCam = item.webCam
            val urlWebCam = webCam.imageLD
            val nameWebCam = webCam.title

            Picasso.with(context).load(urlWebCam)
                    .resize(widthImage, heightImage)
                    .centerCrop()
                    .into(holder.mImageViewWebCam)

            holder.mTextViewNameWebcam.setText(nameWebCam)
            if(hasHeader) {
                holder.mTextViewNameSection.setText(section)
                holder.mTextViewNameSection.visibility = View.VISIBLE
            } else {
                holder.mTextViewNameSection.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                listener?.onWebcamClick(webCam, position)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
            return WebcamHolder(View.inflate(parent.context, R.layout.item_webcam, null))
        }

        override fun getItemCount(): Int {
            return items.size
        }

        class WebcamHolder(view: View) : RecyclerView.ViewHolder(view) {

            val mTextViewNameSection: TextView
            val mTextViewNameWebcam : TextView
            val mImageViewWebCam: ImageView

            init {
                mTextViewNameWebcam = view.findViewById(R.id.mTextViewNameWebcam) as TextView
                mTextViewNameSection = view.findViewById(R.id.mTextViewNameSection) as TextView
                mImageViewWebCam = view.findViewById(R.id.mImageViewWebCam) as ImageView
            }
        }
    }
}

interface OnWebcamClickListener {
    fun onWebcamClick(webcam: Webcam, position: Int)
}
