package com.example.vacationproject2.main

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vacationproject2.Profile.ProfileActivity
import com.example.vacationproject2.R
import com.example.vacationproject2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        setRecyclerView()
        setFabClick()

    }
    private fun setRecyclerView(){
        val adapter = RecyclerAdapter()
        adapter.apply {
            data.add(null)
            data.add(RecyclerData("a","a", listOf("a")))
        }
        binding.recycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.recycler.adapter = adapter
        adapter.notifyDataSetChanged()
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
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION,0f,45f).apply { start() }
        }else{
            ObjectAnimator.ofFloat(binding.fabAdd, "translationY",-400f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabProfile, "translationY",-200f).apply { start() }
            ObjectAnimator.ofFloat(binding.fabMain, View.ROTATION,45f,0f).apply { start() }
        }
        isOpen = !isOpen
    }
}