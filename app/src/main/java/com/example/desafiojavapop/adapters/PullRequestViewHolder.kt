package com.example.desafiojavapop.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiojavapop.databinding.ListPullRequestBinding
import com.example.desafiojavapop.model.PullRequestModel
import com.squareup.picasso.Picasso

class PullRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ListPullRequestBinding.bind(itemView)

    fun bind(item: PullRequestModel, onItemClicked: (String) -> Unit) {

        binding.textViewtitleOfPr.text = item.title
        Picasso.get().load(item.user?.avatarUrl).into(binding.imageViewAuthor)
        binding.textViewBodyDescription.maxLines = 2
        binding.textViewBodyDescription.text = item.body
        binding.textViewAuthorName.text = item.user?.login
        binding.textViewDayOfCreate.text = item.getCreatedAtDateString()

        itemView.setOnClickListener {
            onItemClicked(item.htmlUrl)
        }
    }
}
