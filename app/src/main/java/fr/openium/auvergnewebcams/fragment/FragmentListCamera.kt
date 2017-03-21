package fr.openium.auvergnewebcams.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.salomonbrys.kodein.instance
import com.squareup.picasso.Picasso
import fr.openium.auvergnewebcams.Constants
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.activity.ActivityWebcam
import fr.openium.auvergnewebcams.model.Section
import fr.openium.auvergnewebcams.model.Webcam
import fr.openium.auvergnewebcams.model.adapter.ItemWebcam
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_list_camera.*
import kotlinx.android.synthetic.main.item_webcam.view.*


/**
 * Created by t.coulange on 09/12/2016.
 */
class FragmentListCamera : AbstractFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_list_camera

    protected val mItems = ArrayList<ItemWebcam>()
    protected val picasso: Picasso by kodeinInjector.instance()

    // =================================================================================================================
    // Life cycle
    // =================================================================================================================

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sections = realm!!.where(Section::class.java)
                .findAllSorted(Section::order.name, Sort.ASCENDING)
        for (section in sections) {
            for (webcam in section.webcams) {
                val nameSection = if (section.title == null) "" else section.title!!
                val item = ItemWebcam(webcam, nameSection)
                mItems.add(item)
            }
        }
        initAdapter()
    }

    // =================================================================================================================
    // Specific job
    // =================================================================================================================

    private fun initAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = AdapterWebcam(context, picasso, {
            webcam, _ ->
            val intent = Intent(context, ActivityWebcam::class.java).apply { putExtra(Constants.KEY_ID, webcam.uid) }
            startActivity(intent)
        }, mItems)
    }

    class AdapterWebcam(context: Context, val picasso: Picasso, val listener: ((Webcam, Int) -> Unit)? = null, val items: List<ItemWebcam>) : RecyclerView.Adapter<AdapterWebcam.WebcamHolder>() {
        val heightImage: Int

        init {
            heightImage = context.resources.getDimensionPixelOffset(R.dimen.height_image_list)
//            val display = context.windowManager.getDefaultDisplay()
//            val size = Point()
//            display.getSize(size)
//            widthImage = size.x
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebcamHolder {
            return WebcamHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_webcam, parent, false))
        }

        override fun onBindViewHolder(holder: WebcamHolder, position: Int) {
            val item = items.get(position)

            val section = item.nameSection
            val hasHeader: Boolean
            if (position == 0) {
                hasHeader = true
            } else {
                val prevItem = items.get(position - 1)
                hasHeader = section != prevItem.nameSection
            }

            val webCam = item.webcam
            val urlWebCam = webCam.imageLD
            val nameWebCam = webCam.title

            picasso.load(urlWebCam)
                    .fit()
                    .into(holder.mImageViewWebCam)

            holder.mTextViewNameWebcam.setText(nameWebCam)
            if (hasHeader) {
                holder.mTextViewNameSection.setText(section)
                holder.mTextViewNameSection.visibility = View.VISIBLE
            } else {
                holder.mTextViewNameSection.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {
                listener?.invoke(webCam, position)
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        class WebcamHolder(view: View) : RecyclerView.ViewHolder(view) {
            val mTextViewNameSection: TextView
            val mTextViewNameWebcam: TextView
            val mImageViewWebCam: ImageView

            init {
                mTextViewNameWebcam = view.textViewNameWebcam
                mTextViewNameSection = view.textViewNameSection
                mImageViewWebCam = view.imageViewWebCam
            }
        }
    }
}
