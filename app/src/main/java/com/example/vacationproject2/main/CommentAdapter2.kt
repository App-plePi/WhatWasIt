package com.example.vacationproject2.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.vacationproject2.R
import com.example.vacationproject2.databinding.ItemCommentBinding
import com.example.vacationproject2.databinding.ItemMyCommentBinding
import com.example.vacationproject2.util.FirebaseUtil
import com.example.vacationproject2.util.UtilCode.Companion.VIEW_TYPE_LOADING
import com.example.vacationproject2.util.UtilCode.Companion.VIEW_TYPE_NORMAL

class CommentAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data = mutableListOf<CommentData?>()
    private val VIEW_TYPE_MY = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType){
            VIEW_TYPE_NORMAL ->
            return NormalViewHolder(ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false))
            VIEW_TYPE_MY ->
                return MyViewHolder(ItemMyCommentBinding.inflate(
                    LayoutInflater.from(parent.context),
                parent,false))

            else ->
                return LoadingViewHolder(LayoutInflater.from(parent.context).inflate(
                    R.layout.item_loading,
                        parent,
                        false))

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NormalViewHolder) {
            data[position]?.let { holder.onBind(it) }
        }else if(holder is MyViewHolder){
            data[position]?.let {holder.onBind(it)}
        }
    }

    override fun getItemCount(): Int {
        return if(data == null){
            return 0
        }else{
            return data.size
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (data!!.get(position) == null){
            VIEW_TYPE_LOADING
        }else if(data[position]?.user.equals(FirebaseUtil.getUid())){
            VIEW_TYPE_MY
        }else{
            VIEW_TYPE_NORMAL
        }
    }
}
class NormalViewHolder(var binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(data: CommentData) {
        binding.commentData = data
        FirebaseUtil.getFireStoreInstance().collection("users")
            .document(data.user)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    binding.nickname.text = it.result.get("nickname").toString()
                    FirebaseUtil.storageDownLode(binding.root.context,it.result.get("profile").toString(),
                        binding.profile
                    )
                }
            }
    }
}
class MyViewHolder(var binding: ItemMyCommentBinding) : RecyclerView.ViewHolder(binding.root) {
    fun onBind(data: CommentData) {
        binding.commentData = data
        FirebaseUtil.getFireStoreInstance().collection("users")
            .document(data.user)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    binding.nickname.text = it.result.get("nickname").toString()
                }
            }
    }
}

class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

