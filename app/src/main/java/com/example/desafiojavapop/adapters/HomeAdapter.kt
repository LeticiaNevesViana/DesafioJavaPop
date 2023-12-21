package com.example.desafiojavapop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.desafiojavapop.R
import com.example.desafiojavapop.model.HomeModel

class HomeAdapter(private val onItemClicked: (HomeModel) -> Unit) : RecyclerView.Adapter<HomeViewHolder>() {

    private var repos = mutableListOf<HomeModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_home, parent, false)
        return HomeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return repos.size
    }

    fun setList(repos: List<HomeModel>) {
        this.repos.clear()
        this.repos.addAll(repos  as Collection<HomeModel>)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = repos[position]
        holder.bind(item, onItemClicked)
    }

}