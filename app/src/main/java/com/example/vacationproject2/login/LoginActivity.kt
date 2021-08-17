package com.example.vacationproject2.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.vacationproject2.R
import com.example.vacationproject2.databinding.ActivityLoginBinding
import com.example.vacationproject2.main.MainActivity
import com.example.vacationproject2.util.FirebaseUtil
import com.example.vacationproject2.util.UtilCode.Companion.REQUEST_CODE
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private var isRegister = false

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        isRegister()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        binding.signInBtn.setOnClickListener {
            signIn()
        }
    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseUtil.getAuth().signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                        if (isRegister){
                            val intent = Intent(applicationContext,MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            val intent = Intent(applicationContext,RegisterActivity::class.java)
                            intent.putExtra("userId", FirebaseUtil.getUid())
                            startActivity(intent)
                        }

                }
            }
    }

    private fun isRegister(){
        FirebaseUtil.getFireStoreInstance().collection("users")
            .document(FirebaseUtil.getUid())
            .get().addOnCompleteListener {
                if (it.isSuccessful){
                    isRegister = true
                }
            }
    }
}