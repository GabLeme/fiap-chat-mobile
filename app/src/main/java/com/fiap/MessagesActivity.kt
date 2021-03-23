package com.fiap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fiap.entity.Contact
import com.fiap.entity.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.OnItemClickListener

class MessagesActivity : AppCompatActivity() {


    val adapter = GroupieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_message)
        val rv : RecyclerView = findViewById(R.id.messageRv)
        rv.adapter = adapter

        adapter.setOnItemClickListener(OnItemClickListener { item, view ->
            val chatIntent = Intent(this, ChatActivity::class.java)
            val mItem = item as ContactItem;
            val u = User()
            u.email = mItem.contact.email
            u.uriProfile = mItem.contact.photoUrl
            u.uuid = mItem.contact.uuid
            chatIntent.putExtra("user", u)

            startActivity(chatIntent)
        })
        verifyIfHasUserLoggedIn()
        //fetchLastMessages()
    }

    fun fetchLastMessages() {
        FirebaseFirestore.getInstance().collection("last-messages")
                .document(FirebaseAuth.getInstance().uid!!)
                .collection("contacts")
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    val docChanges = querySnapshot?.documentChanges
                    docChanges.let {
                        Log.i("toaq", it.toString())
                        it!!.forEach {
                            if (it.type == DocumentChange.Type.ADDED) {
                                Log.i("doc", it.document.toString())
                                adapter.add(ContactItem(it.document.toObject(Contact::class.java)))
                            }
                        }
                    }
                }
    }

    fun verifyIfHasUserLoggedIn() {
        if (FirebaseAuth.getInstance().uid == null) {
            val loginIntent = Intent(this, MainActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(loginIntent);
        }
        else {
            fetchLastMessages()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.principal, menu)
        return true
    }

    private class ContactItem(val contact: Contact) : Item<GroupieViewHolder>() {

        override fun getLayout() = R.layout.item_contact_message

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val txEmail = viewHolder.itemView.findViewById<TextView>(R.id.txEmail)
            val txLastMessage = viewHolder.itemView.findViewById<TextView>(R.id.txMessage)
            val imgPhoto = viewHolder.itemView.findViewById<ImageView>(R.id.photoUser)

            txEmail.text = contact.email
            txLastMessage.text = contact.lastMessage

            Picasso.get()
                    .load(contact.photoUrl)
                    .into(imgPhoto)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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