package com.example.vacationproject2.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.vacationproject2.R
import com.example.vacationproject2.databinding.ActivityRegisterBinding
import com.example.vacationproject2.main.MainActivity
import com.example.vacationproject2.util.FirebaseUtil
import com.example.vacationproject2.util.UtilCode.Companion.REQUEST_CODE
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var url = null

    private var isChange = false
    private var isLimit = false
    private var isLoading = false
    private var isCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_register)

        isChange = intent.getBooleanExtra("change",false)
        setData()

        binding.profileButton.setOnClickListener {
            getImage()
        }

        binding.startButton.setOnClickListener {
            if (!isLimit && !isLoading && isCheck){
                val nickname = hashMapOf(
                    "nickname" to binding.nicknameEdit.text.toString(),
                    "profile" to if (url == null){ "default" }else { FirebaseUtil.firebaseUpload(this, url) }
                )
                setAccount(nickname)
                if(isChange){
                    finish()
                }else{
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        binding.nicknameEdit.addTextChangedListener(textWatcher)

        binding.checkButton.setOnClickListener {
            if (!isLimit && !isLoading){
                checkNickname()
            }
        }

        binding.profileButton.setOnClickListener {
            getImage()
        }
    }

    private val textWatcher = object: TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            binding.checkButton.setBackgroundDrawable(ContextCompat.getDrawable(applicationContext,R.drawable.edit_background1))
            binding.checkButton.setText("중복확인")
            binding.checkButton.setTextColor(Color.parseColor("#3d3d3d"))
            val textLength = binding.nicknameEdit.text.length
            if(textLength > 13 || textLength < 2){
                binding.nicknameLimit.setTextColor(Color.parseColor("#FF7474"))
                isLimit = true
            }else{
                binding.nicknameLimit.setTextColor(Color.parseColor("#909090"))
                isLimit = false
            }
        }
    }

    private fun checkNickname(){
        isLoading = true
        FirebaseUtil.getFireStoreInstance().collection("users")
            .whereEqualTo("nickname",binding.nicknameEdit.text.toString())
            .get().addOnCompleteListener {
                if (!it.result.isEmpty) {
                    Toast.makeText(this,"이미 존재하는 닉네임입니다",Toast.LENGTH_SHORT).show()
                    binding.checkButton.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.edit_background1))
                    binding.checkButton.setText("중복확인")
                    binding.checkButton.setTextColor(Color.parseColor("#3d3d3d"))
                    isCheck = false
                }else{
                    binding.checkButton.setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.button_background))
                    binding.checkButton.setText("확인완료")
                    binding.checkButton.setTextColor(Color.parseColor("#ffffff"))
                    isCheck = true
                }
                isLoading = false
            }
    }

    private fun setData(){
        if (isChange){
            binding.startButton.setText("변경하기")
            FirebaseUtil.getFireStoreInstance().collection("users")
                .document(FirebaseUtil.getUid())
                .get()
                .addOnCompleteListener {
                    binding.nicknameEdit.setText(it.result.get("nickname").toString())
                    FirebaseUtil.storageDownLode(this,it.result.get("profile").toString(),binding.profileButton)
                }
        }
    }

    fun setAccount(nickname: HashMap<String, String?>){
        FirebaseUtil.getFireStoreInstance().collection("users")
            .document(FirebaseUtil.getUid())
            .set(nickname)
            .addOnFailureListener {
                Log.d(TAG, "addAccount: 실패 "+it.message)
            }
    }
    private fun getImage(){
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