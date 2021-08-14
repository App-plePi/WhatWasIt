package com.example.vacationproject2.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vacationproject2.R
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class RecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_NORMAL = 0
    private val VIEW_TYPE_LOADING = 1

    var data = ArrayList<RecyclerData?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_NORMAL){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item,parent,false)
            return NormalViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loading_item,parent,false)
            return LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NormalViewHolder){
            data.get(position)?.let { holder.onBind(it) }
        }
    }

    override fun getItemCount(): Int {
        if(data == null){
            return 0
        }else{
            return data!!.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (data!!.get(position) == null){
            return VIEW_TYPE_LOADING
        }else{
            return VIEW_TYPE_NORMAL
        }
    }


    class NormalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        private val nickname:TextView = itemView.findViewById(R.id.nickname)
        private val contents: TextView = itemView.findViewById(R.id.contents)
        private val keywordRecycler: RecyclerView = itemView.findViewById(R.id.keywordList)

        fun onBind(data:RecyclerData){
            val adapter = KeywordAdapter()
            val keywordList = ArrayList<String>()
            for (i in 0..data.keyword.size-1){
                keywordList.add(data.keyword.get(i))
            }
            adapter.apply {
                this.data = keywordList
            }
            nickname.text = data.nickname
            contents.text = data.contents
            FlexboxLayoutManager(itemView.context).apply {
                flexWrap = FlexWrap.WRAP
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
            }.let {
                keywordRecycler.layoutManager = it
                keywordRecycler.adapter = adapter
            }
            itemView.setOnClickListener {

            }
        }
    }
    class LoadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }


}