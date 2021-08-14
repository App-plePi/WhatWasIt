package com.example.vacationproject2.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*

class FirebaseUtil {
    companion object {

        fun firebaseUpload(context: Context?, uri: Uri?): String? {
            val storage = FirebaseStorage.getInstance("gs://mobile-contents-812ea.appspot.com")
            var imgname = ""
            if (uri != null) {
                val uuid = UUID.randomUUID().toString()
                imgname = "images/$uuid"
                val storageReference = storage.getReferenceFromUrl("gs://vacationproject-7078e.appspot.com")
                        .child(imgname)
                storageReference.putFile(uri).addOnSuccessListener {
                    Log.d(
                        ContentValues.TAG,
                        "onSuccess: 성공"
                    )
                }.addOnFailureListener {
                    Toast.makeText(context, "업로드에 실패하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
            return imgname
        }

        fun storageDownLode (context: Context, name: String, imageView: ImageView){
            val storage = FirebaseStorage.getInstance("gs://vacationproject-7078e.appspot.com")
            val storageRef = storage.reference
            storageRef.child(name).downloadUrl.addOnSuccessListener {
                Glide.with(context).load(it).into(imageView)
            }.addOnFailureListener {
            }
        }

        fun storageDownLode (context: Context, name: String, imageButton: ImageButton){
            val storage = FirebaseStorage.getInstance("gs://vacationproject-7078e.appspot.com")
            val storageRef = storage.reference
            storageRef.child(name).downloadUrl.addOnSuccessListener {
                Glide.with(context).load(it).into(imageButton)
            }.addOnFailureListener {

            }
        }

        fun getUid () : String {
            return FirebaseAuth.getInstance().uid!!
        }

        fun getFireStoreInstance () : FirebaseFirestore {
            return FirebaseFirestore.getInstance();
        }

        fun getAuth () : FirebaseAuth {
            return FirebaseAuth.getInstance();
        }

    }
}