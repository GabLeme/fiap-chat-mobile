package com.fiap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btn_login)
        val editTextEmail = findViewById<EditText>(R.id.editTxtEmail)
        val editTextPass = findViewById<EditText>(R.id.editTxtPass)

        val txtRegister = findViewById<TextView>(R.id.txt_register)

        btnLogin.setOnClickListener(View.OnClickListener { v ->

        })


        txtRegister.setOnClickListener(View.OnClickListener { v ->
            val regIntent = Intent(this, RegisterActivity::class.java)
            startActivity(regIntent)
        })

    }


}

