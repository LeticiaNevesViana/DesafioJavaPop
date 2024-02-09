package com.example.desafiojavapop.adapters

import PullRequestViewHolder
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiojavapop.R
import com.example.desafiojavapop.model.PullRequestModel
import java.lang.ref.WeakReference


class PullRequestAdapter(private val onItemClicked: (String) -> Unit) : RecyclerView.Adapter<PullRequestViewHolder>() {
    private var pullRequests = mutableListOf<PullRequestModel>()

    private val onItemClickedRef = WeakReference(onItemClicked)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PullRequestViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_pull_request, parent, false)
        return PullRequestViewHolder(itemView, onItemClickedRef)
    }

    override fun onBindViewHolder(holder: PullRequestViewHolder, position: Int) {
        val item = pullRequests[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = pullRequests.size

    fun setList(pullRequestList: List<PullRequestModel>) {
        pullRequests.clear()
        pullRequests.addAll(pullRequestList)
        notifyDataSetChanged()
    }
}


