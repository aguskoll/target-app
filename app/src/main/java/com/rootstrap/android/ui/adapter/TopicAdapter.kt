package com.rootstrap.android.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rootstrap.android.R
import com.rootstrap.android.databinding.ItemTopicBinding

import com.rootstrap.android.network.models.Topic
import com.rootstrap.android.util.extensions.inflate

class TopicAdapter(
    private val topics: List<Topic>,
    private val onItemSelected: (Topic) -> Unit
) : RecyclerView.Adapter<TopicViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = parent.inflate(R.layout.item_topic)
        return TopicViewHolder(view)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val item = topics[position]
        holder.binding.nameTopicText.text = item.label
        Glide.with(holder.itemView)
            .load(item.icon)
            .centerCrop()
            .into(holder.binding.iconTopic)
        holder.bindListener(onItemSelected, item)
    }
}

class TopicViewHolder(val item: View) : RecyclerView.ViewHolder(item) {
    val binding = ItemTopicBinding.bind(item)

    fun bindListener(onItemSelected: (Topic) -> Unit, topic: Topic) {
        binding.topicContainer.setOnClickListener {
            onItemSelected(topic)
        }
    }
}
