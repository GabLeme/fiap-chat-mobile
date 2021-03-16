package com.fiap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore

class ContactsActivity : AppCompatActivity() {
//
//    private lateinit var adapter:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_contacts)
        val groupAda =
        fetchUsers()
    }

    fun fetchUsers() {
        FirebaseFirestore.getInstance().collection("users")
            .addSnapshotListener(EventListener { e, error ->
                val docs = e?.documents
                docs?.forEach { doc ->
                    Log.i("users", doc.id)
                }
            })
    }
}