package com.example.vacationproject2.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vacationproject2.R
import com.example.vacationproject2.util.FirebaseUtil
import com.example.vacationproject2.util.UtilCode.Companion.VIEW_TYPE_LOADING
import com.example.vacationproject2.util.UtilCode.Companion.VIEW_TYPE_NORMAL
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class RecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var data = ArrayList<RecyclerData?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_NORMAL){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler,parent,false)
            return NormalViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading,parent,false)
            return LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NormalViewHolder){
            data[position]?.let { holder.onBind(it) }
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
        private val nickname: TextView = itemView.findViewById(R.id.nickname)
        private val contents: TextView = itemView.findViewById(R.id.contents)
        private val keywordRecycler: RecyclerView = itemView.findViewById(R.id.keywordRecycler)

        fun onBind(data: RecyclerData) {
            val adapter = CreateKeywordAdapter()
            val keywordList = ArrayList<String>()
            if (!data.keyword.isEmpty()) {
                for (i in 0..data.keyword.size - 1) {
                    keywordList.add(data.keyword.get(i))
                }
                adapter.apply {
                    this.data = keywordList
                }
                FlexboxLayoutManager(itemView.context).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.FLEX_START
                }.let {
                    keywordRecycler.layoutManager = it
                    keywordRecycler.adapter = adapter
                }
            } else {
                keywordRecycler.visibility = View.GONE
            }

            FirebaseUtil.getFireStoreInstance().collection("users")
                .document(data.user)
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        nickname.text = it.result.get("nickname").toString()
                        FirebaseUtil.storageDownLode(
                            itemView.context,
                            it.result.get("profile").toString(),
                            profileImage
                        )
                    }
                }
            contents.text = data.contents

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, SeeMoreActivity::class.java)
                intent.putExtra("id", data.postNumber)
                it.context.startActivity(intent)
            }
        }
    }
    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}