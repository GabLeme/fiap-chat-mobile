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
import com.fiap.entity.User
import com.fiap.utils.StringUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*
import kotlin.math.log

class RegisterActivity : AppCompatActivity() {
    private lateinit var selectedUri: Uri
    private lateinit var imgPhoto: ImageView
    private lateinit var btnPhoto: Button
    private val PICK_IMAGE = 0
    private lateinit var uriProfile: String
    private val utils = StringUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_register)

        val editEmail = findViewById<EditText>(R.id.editTxtEmail)
        val editPass = findViewById<EditText>(R.id.editTxtPass)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        btnPhoto = findViewById(R.id.btnPhoto)
        imgPhoto = findViewById(R.id.img_photo)
        btnPhoto.setOnClickListener(View.OnClickListener { v ->
            selectPhotoFromGalery()
        })

        btnRegister.setOnClickListener(View.OnClickListener { v ->
            createAuthentication(editEmail.text.toString(), editPass.text.toString())
        })
    }


    fun createAuthentication(email: String, password: String) {
        if(utils.isNullOrEmpty(email) || utils.isNullOrEmpty(password)) {
            makeToast("E-mail e senha devem ser preenchidos")
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(OnCompleteListener {
            if(it.isSuccessful) {
                it.result?.user?.let { it1 -> createUserInstance(it1.uid, email)}
                //saveImg()
            }
            // tratar os demais itens (it)
        }).addOnFailureListener(OnFailureListener {
            Log.i("Failure", it.message.toString())
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE) {
            Log.i("values", data.toString())
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
        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.type = "image/*"

        val intents = arrayOf(pickIntent)

        val chooserIntent = Intent.createChooser(getIntent, "Selecione uma foto")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents)

        startActivityForResult(chooserIntent, PICK_IMAGE)
    }

    fun createUserInstance(uuid: String, email: String) {
        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/${fileName}")
        ref.putFile(selectedUri).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                val userToBeSaved = User(uuid, email, it.toString())
                FirebaseFirestore.getInstance().collection("users")
                        .document(uuid)
                        .set(userToBeSaved)
                        .addOnSuccessListener {
                            val principalIntent = Intent(this, MessagesActivity::class.java)
                            principalIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(principalIntent)
                          //  Log.i("userAdded", it.get().result?.id.toString())
                        }
            }
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}