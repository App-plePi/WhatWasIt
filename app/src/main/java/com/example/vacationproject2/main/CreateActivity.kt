package com.example.vacationproject2.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.vacationproject2.R
import com.example.vacationproject2.databinding.ActivityCreateBinding
import com.example.vacationproject2.dialog.AddKeywordDialog
import com.example.vacationproject2.util.FirebaseUtil
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class CreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateBinding
    private val adapter = CreateKeywordAdapter()
    private var postNumber : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_create)
        getPostNumber()
        setRecycler()
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.addKeywordButton.setOnClickListener {
            setKeyword()
        }
        binding.createButton.setOnClickListener {
            if (binding.contentsEdit.text.isEmpty()) {
                Toast.makeText(this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            }else{
                createPost()
            }
        }
    }
    private fun getPostNumber(){
        FirebaseUtil.getFireStoreInstance().collection("posts")
            .document("information")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    postNumber = it.result.get("number").toString().toInt()
                }
            }
    }

    private fun setRecycler(){
        binding.keywordRecycler
        FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }.let {
            binding.keywordRecycler.layoutManager = it
            binding.keywordRecycler.adapter = adapter
        }
    }
    private fun setKeyword(){
        val dialog = AddKeywordDialog(adapter)
        dialog.show(supportFragmentManager, "keywordDialog" )
    }



    private fun createPost(){
        val map = hashMapOf(
            "keyword" to adapter.data,
            "contents" to binding.contentsEdit.text.toString(),
            "user" to FirebaseUtil.getAuth().uid,
            "postNumber" to postNumber+1
        )
        FirebaseUtil.getFireStoreInstance().collection("posts")
            .document((postNumber+1).toString())
            .set(map)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    FirebaseUtil.setPostNumber(postNumber)
                    finish()
                }else{
                    finish()
                    Toast.makeText(this,"업로드에 실패하였습니다",Toast.LENGTH_SHORT).show()
                }
            }
    }
}