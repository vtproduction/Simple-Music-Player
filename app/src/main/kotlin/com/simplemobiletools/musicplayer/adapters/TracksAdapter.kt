package com.simplemobiletools.musicplayer.adapters

import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.extensions.beGone
import com.simplemobiletools.commons.extensions.beVisible
import com.simplemobiletools.commons.extensions.getColoredDrawableWithColor
import com.simplemobiletools.commons.extensions.getFormattedDuration
import com.simplemobiletools.commons.views.FastScroller
import com.simplemobiletools.commons.views.MyRecyclerView
import com.simplemobiletools.musicplayer.R
import com.simplemobiletools.musicplayer.activities.SimpleActivity
import com.simplemobiletools.musicplayer.extensions.addTracksToPlaylist
import com.simplemobiletools.musicplayer.extensions.addTracksToQueue
import com.simplemobiletools.musicplayer.models.Track
import kotlinx.android.synthetic.main.item_track.view.*
import java.util.*

class TracksAdapter(activity: SimpleActivity, val tracks: ArrayList<Track>, recyclerView: MyRecyclerView, fastScroller: FastScroller, itemClick: (Any) -> Unit) :
        MyRecyclerViewAdapter(activity, recyclerView, fastScroller, itemClick) {

    private val placeholder = resources.getColoredDrawableWithColor(R.drawable.ic_headset, textColor)

    init {
        setupDragListener(true)
    }

    override fun getActionMenuId() = R.menu.cab_tracks

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = createViewHolder(R.layout.item_track, parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = tracks.getOrNull(position) ?: return
        holder.bindView(track, true, true) { itemView, layoutPosition ->
            setupView(itemView, track)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = tracks.size

    override fun prepareActionMode(menu: Menu) {}

    override fun actionItemPressed(id: Int) {
        if (selectedKeys.isEmpty()) {
            return
        }

        when (id) {
            R.id.cab_add_to_playlist -> addToPlaylist()
            R.id.cab_add_to_queue -> addToQueue()
        }
    }

    override fun getSelectableItemCount() = tracks.size

    override fun getIsItemSelectable(position: Int) = true

    override fun getItemSelectionKey(position: Int) = tracks.getOrNull(position)?.hashCode()

    override fun getItemKeyPosition(key: Int) = tracks.indexOfFirst { it.hashCode() == key }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    private fun addToPlaylist() {
        activity.addTracksToPlaylist(getSelectedTracks()) {
            finishActMode()
            notifyDataSetChanged()
        }
    }

    private fun addToQueue() {
        activity.addTracksToQueue(getSelectedTracks()) {
            finishActMode()
        }
    }

    private fun getSelectedTracks(): List<Track> = tracks.filter { selectedKeys.contains(it.hashCode()) }.toList()

    private fun setupView(view: View, track: Track) {
        view.apply {
            track_frame?.isSelected = selectedKeys.contains(track.hashCode())
            track_title.text = track.title

            arrayOf(track_id, track_title, track_duration).forEach {
                it.setTextColor(textColor)
            }

            track_duration.text = track.duration.getFormattedDuration()
            val options = RequestOptions()
                .error(placeholder)
                .transform(CenterCrop(), RoundedCorners(8))

            Glide.with(activity)
                .load(track.coverArt)
                .apply(options)
                .into(findViewById(R.id.track_image))

            track_image.beVisible()
            track_id.beGone()
        }
    }
}
