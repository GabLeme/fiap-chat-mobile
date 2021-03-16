package com.fiap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth

class MessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_message)
        verifyIfHasUserLoggedIn()
    }

    fun verifyIfHasUserLoggedIn() {
        if(FirebaseAuth.getInstance().uid == null) {
            val loginIntent = Intent(this, MainActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(loginIntent);
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
             R.id.contacts -> {
                val contactIntent = Intent(this, ContactsActivity::class.java)
                 startActivity(contactIntent)
             }
             R.id.logout -> {
                 FirebaseAuth.getInstance().signOut()
                 verifyIfHasUserLoggedIn()
             }

        }
        return super.onOptionsItemSelected(item)
    }
}