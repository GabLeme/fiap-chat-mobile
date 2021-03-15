package com.fiap

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var selectedUri: Uri
    private lateinit var imgPhoto: ImageView
    private lateinit var btnPhoto: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_register)
        auth = Firebase.auth

        val editEmail = findViewById<EditText>(R.id.editTxtEmail)
        val editPass = findViewById<EditText>(R.id.editTxtPass)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        btnPhoto = findViewById(R.id.btnPhoto)
        imgPhoto = findViewById(R.id.img_photo)
        btnPhoto.setOnClickListener(View.OnClickListener { v ->
            selectPhotoFromGalery()
        })

        btnRegister.setOnClickListener(View.OnClickListener { v ->
            regUser(editEmail.text.toString(), editPass.text.toString())
        })
    }


    fun regUser(email: String, password: String) {
        if(isNullOrEmpty(email) || isNullOrEmpty(password)) {
            Toast.makeText(this, "E-mail e senha devem ser preenchidos", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(OnCompleteListener {
            if(it.isSuccessful) {
                it.result?.user?.let { it1 -> Log.i("id", it1.uid) }
            }
            // tratar os demais itens (it)
        }).addOnFailureListener(OnFailureListener {
            Log.i("Failure", it.message.toString())
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == 0) {
            selectedUri = data?.data!!
            // verificar build e realizar tratativas < 28  || > 28
            val bitmap = MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                selectedUri
            )
            imgPhoto.setImageDrawable(BitmapDrawable(bitmap))
            btnPhoto.alpha = 0F
        }
    }

    fun selectPhotoFromGalery() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(pickIntent, 0)

    }


    fun isNullOrEmpty(str: String?): Boolean {
        if (str != null && !str.trim().isEmpty())
            return false
        return true
    }
}