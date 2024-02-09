package com.example.desafiojavapop.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiojavapop.databinding.ListHomeBinding
import com.example.desafiojavapop.model.HomeModel
import com.squareup.picasso.Picasso

 class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ListHomeBinding.bind(itemView)

    fun bind(item: HomeModel, onItemClicked: (HomeModel) -> Unit) {
        binding.textViewRepoName.text = item.repo
        Picasso.get().load(item.owner?.avatarUrl).into(binding.imageViewAuthor)
        binding.textViewRepoDescription.maxLines = 2
        binding.textViewRepoDescription.text = item.description
        binding.textViewAuthorName.text = item.owner?.login
        binding.textViewForks.text = item.forksCount.toString()
        binding.textViewStars.text = item.starsCount.toString()

        itemView.setOnClickListener {
            onItemClicked(item)
        }
    }
}
