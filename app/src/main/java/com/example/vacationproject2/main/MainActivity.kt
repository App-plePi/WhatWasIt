package com.example.vacationproject2.main

import android.animation.ObjectAnimator
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vacationproject2.Profile.ProfileActivity
import com.example.vacationproject2.R
import com.example.vacationproject2.databinding.ActivityMainBinding
import com.example.vacationproject2.util.FirebaseUtil
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isOpen = false
    private var isLoading = false
    private var lastDocument: DocumentSnapshot? = null
    private val adapter = RecyclerAdapter()
    private var listSize : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        setFabClick()

        infiniteScroll()
        binding.swipe.setOnRefreshListener {
            adapter.apply {
                data.clear()
            }
            adapter.notifyDataSetChanged()
            lastDocument = null
            if (!isLoading){
                getItem()
            }
            binding.swipe.isRefreshing = false
        }

    }

    override fun onStart() {
        super.onStart()
        setRecyclerView()
    }


    private fun setRecyclerView(){
        binding.recycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.recycler.adapter = adapter
        if (!isLoading){
            adapter.data.clear()
            getItem()
        }
    }

    private fun setQuery(): Query {
        val collectionReference = FirebaseUtil.getFireStoreInstance().collection("posts")
        return if (lastDocument == null){
            collectionReference.orderBy("postNumber", Query.Direction.DESCENDING)
                .limit(15)
        }else{
            collectionReference.orderBy("postNumber", Query.Direction.DESCENDING)
                .startAfter(lastDocument).limit(15)
        }
    }

    private fun getItem(){
        isLoading = true
        adapter.apply {
            data.add(null)
            listSize = data.size
        }
        adapter.notifyItemInserted(listSize-1)
        setQuery().get().addOnCompleteListener {
                if (it.isSuccessful){
                    adapter.apply {
                        data.removeAt(listSize-1)
                    }
                    adapter.notifyItemRemoved(listSize-1)
                    for (doc in it.result){
                        adapter.apply {
                            data.add(doc.toObject(RecyclerData::class.java))
                        }
                    }
                    adapter.notifyDataSetChanged()
                    if (it.result.documents.size != 0){
                        lastDocument = it.result.documents[it.result.documents.size-1]
                    }
                }
            }
        isLoading = false
    }

    private fun infiniteScroll(){
        binding.recycler.setOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val manager = recyclerView.layoutManager as LinearLayoutManager
                if (!isLoading && manager.findLastCompletelyVisibleItemPosition() == listSize-1 &&recyclerView.canScrollVertically(1)&& lastDocument != null){
                    getItem()
                    Log.d(TAG, "onScrolled: "+lastDocument.toString())
                }
            }
        })
    }


    private fun setFabClick(){
        binding.fabMain.setOnClickListener {
            toggleFab()
        }

        binding.fabProfile.setOnClickListener {
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this,CreateActivity::class.java)
            startActivity(intent)
        }
    }

    private fun toggleFab(){
        if(isOpen){
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY",0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabProfile, "translationY",0f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION,45f,0f).apply { start() }
        }else{
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY",-380f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabProfile, "translationY",-200f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION,0f,45f).apply { start() }
        }
        isOpen = !isOpen
    }
}