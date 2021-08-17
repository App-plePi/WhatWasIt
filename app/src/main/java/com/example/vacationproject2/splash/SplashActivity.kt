package com.example.vacationproject2.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.vacationproject2.R
import com.example.vacationproject2.login.LoginActivity
import com.example.vacationproject2.main.MainActivity
import com.example.vacationproject2.util.FirebaseUtil
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {
    private var isRegister = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        CoroutineScope(Dispatchers.IO).launch {
            isRegister()
            delay(2000)

            if (FirebaseUtil.getAuth().currentUser != null && isRegister){
                val intent = Intent(applicationContext,MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(applicationContext,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    private fun isRegister(){
        if (FirebaseUtil.getUid() != null){
            FirebaseUtil.getFireStoreInstance().collection("users")
                .document(FirebaseUtil.getUid())
                .get().addOnCompleteListener {
                    if (it.isSuccessful){
                        isRegister = true
                    }
                }
        }
    }
}