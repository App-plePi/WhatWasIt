package com.example.vacationproject2.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vacationproject2.R

class SeeMoreKeywordAdapter(): RecyclerView.Adapter<SeeMoreKeywordAdapter.ViewHolder>() {

    var data = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeeMoreKeywordAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_keyword_big,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeeMoreKeywordAdapter.ViewHolder, position: Int) {
        val keyword = data.get(position)
        holder.onBind(keyword)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val keywordText: TextView = itemView.findViewById(R.id.keyword)
        fun onBind(keyword: String){
            keywordText.text = keyword
        }
    }
}