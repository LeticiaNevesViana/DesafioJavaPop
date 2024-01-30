package com.example.desafiojavapop.adapters

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiojavapop.R
import com.example.desafiojavapop.model.PullRequestModel
import kotlin.reflect.KFunction1

class PullRequestAdapter(private val onItemClicked: KFunction1<String, Unit>) : RecyclerView.Adapter<PullRequestViewHolder>() {

    private var pullRequests = mutableListOf<PullRequestModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PullRequestViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_pull_request, parent, false)
        return PullRequestViewHolder(itemView)
    }

    fun setList(pullRequestList: List<PullRequestModel>) {
        this.pullRequests.clear()
        this.pullRequests.addAll(pullRequestList)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PullRequestViewHolder, position: Int) {
        val item = pullRequests[position]
        holder.bind(item, onItemClicked)
    }

    override fun getItemCount(): Int {
        return pullRequests.size
    }
}












