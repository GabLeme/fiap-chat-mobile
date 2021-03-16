package com.fiap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.fiap.utils.StringUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val utils: StringUtils = StringUtils()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btn_login)
        val editTextEmail = findViewById<EditText>(R.id.editTxtEmail)
        val editTextPass = findViewById<EditText>(R.id.editTxtPass)
        auth = Firebase.auth
        val txtRegister = findViewById<TextView>(R.id.txt_register)

        btnLogin.setOnClickListener(View.OnClickListener { v ->
            authenticate(editTextEmail.text.toString(), editTextPass.text.toString())
        })

        txtRegister.setOnClickListener(View.OnClickListener { v ->
            val regIntent = Intent(this, RegisterActivity::class.java)
            startActivity(regIntent)
        })
    }

    fun authenticate(email: String, password: String) {
        if(utils.isNullOrEmpty(email) || utils.isNullOrEmpty(password)) {
            makeToast("E-mail e senha devem ser preenchidos")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener {
                if(it.isSuccessful)
                    it.result?.user?.let { it1 -> Log.i("a", it1.uid) }
                else {
                    makeToast("Falha ao realizar autenticacao")
                }
            })
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}

