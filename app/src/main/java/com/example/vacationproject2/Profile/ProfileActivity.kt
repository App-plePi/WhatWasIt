package com.example.vacationproject2.Profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vacationproject2.R
import com.example.vacationproject2.databinding.ActivityProfileBinding
import com.example.vacationproject2.login.LoginActivity
import com.example.vacationproject2.login.RegisterActivity
import com.example.vacationproject2.main.RecyclerAdapter
import com.example.vacationproject2.main.RecyclerData
import com.example.vacationproject2.splash.SplashActivity
import com.example.vacationproject2.util.FirebaseUtil
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlin.system.exitProcess

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_profile)

        setRecycler()
        setProfile()

        binding.changeProfile.setOnClickListener {
            intent = Intent(this,RegisterActivity::class.java)

            intent.putExtra("change", true)
            startActivity(intent)
        }

        binding.signOutButton.setOnClickListener {
                signOut()
        }
    }

    private fun setRecycler(){
        val adapter = RecyclerAdapter()
        binding.profileRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.profileRecycler.adapter = adapter
        getPost(adapter)
    }

    private fun setProfile(){
        FirebaseUtil.getFireStoreInstance().collection("users")
            .document(FirebaseUtil.getUid())
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    binding.nickname.text = it.result.get("nickname").toString()
                    FirebaseUtil.storageDownLode(this,it.result.get("profile").toString(),binding.profile)
                }
            }
    }


    private fun getPost(adapter: RecyclerAdapter){
        FirebaseUtil.getFireStoreInstance().collection("posts")
            .whereEqualTo("user",FirebaseUtil.getUid()).orderBy("postNumber",Query.Direction.DESCENDING).get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    for (doc in it.result){
                        adapter.apply {
                            data.add(doc.toObject(RecyclerData::class.java))
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun signOut(){
        finishAffinity()

        FirebaseUtil.getAuth().signOut()
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)

    }

}