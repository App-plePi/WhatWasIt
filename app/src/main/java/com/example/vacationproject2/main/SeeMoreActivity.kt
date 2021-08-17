package com.example.vacationproject2.main

import android.content.ContentValues.TAG
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vacationproject2.R
import com.example.vacationproject2.databinding.ActivitySeeMoreBinding
import com.example.vacationproject2.util.FirebaseUtil
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlin.properties.Delegates

class SeeMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeeMoreBinding
    private var lastDocument: DocumentSnapshot? = null
    private val adapter = CommentAdapter()
    private var isLoading = false
    private var listSize = 0
    private var commentNumber = 0
    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_more)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_see_more)
        id = intent.getIntExtra("id",0)

        getInformation()

        setData()
        setCommentRecycler()

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.postButton.setOnClickListener {
            if (binding.commentEidt.text.toString().isEmpty()){
                Toast.makeText(this,"내용을 입력해주세요",Toast.LENGTH_SHORT).show()
            }else{
                postComment(binding.commentEidt.text.toString())
                val manager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
                binding.commentEidt.text.clear()
            }
        }

        binding.swipe.setOnRefreshListener {
            adapter.apply {
                data.clear()
            }
            adapter.notifyDataSetChanged()
            lastDocument = null
            if (!isLoading){
                setComment()
            }
            binding.swipe.isRefreshing = false
        }
    }
    private fun setData(){
        FirebaseUtil.getFireStoreInstance().collection("posts")
            .document(id.toString())
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    binding.contents.text = it.result.get("contents").toString()
                    
                    FirebaseUtil.getFireStoreInstance().collection("users")
                        .document(it.result.get("user").toString())
                        .get().addOnCompleteListener {
                            binding.nickname.text = it.result.get("nickname").toString()
                        }
                    
                    FirebaseUtil.storageDownLode(this,it.result.get("profile").toString(),binding.profile)
                    setKeywordRecycler(it.result.get("keyword") as MutableList<String>)
                }else{
                    finish()
                    Toast.makeText(this,"불러오지 못했습니다",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setKeywordRecycler(keyword: MutableList<String>){
        val adapter = SeeMoreKeywordAdapter()
        binding.keywordRecycler
        FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }.let {
            binding.keywordRecycler.layoutManager = it
            binding.keywordRecycler.adapter = adapter
        }
        adapter.apply {
            data = keyword
        }
    }

    private fun setCommentRecycler(){
        binding.commentRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.commentRecycler.adapter = adapter
        setComment()
    }

    private fun postComment(comment: String){
        val map = hashMapOf(
            "comment" to comment,
            "user" to FirebaseUtil.getUid(),
            "postNumber" to id,
            "commentNumber" to commentNumber+1
         )
        FirebaseUtil.getFireStoreInstance().collection("comment")
            .document((commentNumber+1).toString())
            .set(map)
        setInformation()
        adapter.data.clear()
        adapter.notifyDataSetChanged()
        lastDocument = null
        setComment()
    }

    private fun setInformation(){
        val map = hashMapOf(
            "number" to commentNumber+1
        )
        FirebaseUtil.getFireStoreInstance().collection("comment")
            .document("information")
            .set(map)
    }

    private fun getInformation(){
        FirebaseUtil.getFireStoreInstance().collection("comment")
            .document("information")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    commentNumber = it.result.get("number").toString().toInt()
                }
            }
    }

    private fun setComment(){
        isLoading = true
        adapter.apply {
            data.add(null)
            listSize = data.size
        }
        adapter.notifyItemInserted(listSize-1)
        setQuery().get().addOnCompleteListener {
            adapter.apply {
                data.removeAt(listSize-1)
            }
            adapter.notifyItemRemoved(listSize-1)
            if (it.isSuccessful){
                for(doc in it.result){
                    adapter.apply {
                        data.add(doc.toObject(CommentData::class.java))
                    }
                }
                adapter.notifyDataSetChanged()
                if (it.result.documents.size != 0){
                    lastDocument = it.result.documents[it.result.documents.size-1]
                }
            }else{
                Log.d(TAG, "setComment: "+it.exception)

            }
        }
        isLoading = false
    }

    private fun setQuery(): Query {
        val collectionReference = FirebaseUtil.getFireStoreInstance().collection("comment")
        if (lastDocument == null){
            return collectionReference.whereEqualTo("postNumber",id)
                .orderBy("commentNumber").limit(30)
        }else{
            return collectionReference.whereEqualTo("postNumber",id)
                .orderBy("commentNumber").startAfter(lastDocument).limit(30)
        }
    }

}