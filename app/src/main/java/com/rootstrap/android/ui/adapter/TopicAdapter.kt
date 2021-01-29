package com.rootstrap.android.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rootstrap.android.R
import com.rootstrap.android.databinding.ItemTopicBinding
import com.rootstrap.android.models.TopicModel

import com.rootstrap.android.util.extensions.inflate

class TopicAdapter(
    private var topics: List<TopicModel>,
    private val onItemSelected: (TopicModel) -> Unit
) : RecyclerView.Adapter<TopicViewHolder>() {

    val itemsCopy = mutableListOf<TopicModel>()

    init {
        itemsCopy.addAll(topics)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = parent.inflate(R.layout.item_topic)
        return TopicViewHolder(view)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    fun filter(text: String) {
        topics = itemsCopy.filter { it.label.toLowerCase().contains(text) }
        notifyDataSetChanged()
    }

    fun clearFilter() {
        topics = itemsCopy
        notifyDataSetChanged()
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

    fun bindListener(onItemSelected: (TopicModel) -> Unit, topic: TopicModel) {
        binding.topicContainer.setOnClickListener {
            onItemSelected(topic)
        }
    }
}
