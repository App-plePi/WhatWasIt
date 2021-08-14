package com.example.vacationproject2.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.vacationproject2.R
import com.example.vacationproject2.databinding.ActivityRegisterBinding
import com.example.vacationproject2.main.MainActivity
import com.example.vacationproject2.util.FirebaseUtil
import com.example.vacationproject2.util.UtilCode.Companion.REQUEST_CODE
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import java.util.jar.Manifest
import kotlin.math.log

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_register)


        binding.profileButton.setOnClickListener {

        }
        binding.startButton.setOnClickListener {
            val nickname = hashMapOf(
                "nickname" to binding.nicknameEdit.text.toString()
            )
            addAccount(nickname)

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        binding.profileButton.setOnClickListener {
            setProfile()
        }
    }

    fun addAccount(nickname: HashMap<String,String>){
        FirebaseUtil.getFireStoreInstance().collection("users")
            .document(FirebaseUtil.getUid())
            .set(nickname)
            .addOnFailureListener {
                Log.d(TAG, "addAccount: 실패 "+it.message)
            }
    }
    fun setProfile(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CODE){
                 var url : Uri? = data?.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,url)
                    binding.profileButton.setImageBitmap(bitmap)
                }catch (e:Exception){

                }
            }else{

            }
        }
    }
}