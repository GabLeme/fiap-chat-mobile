package com.fiap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fiap.entity.User
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


class ContactsActivity : AppCompatActivity() {

    val adapter = GroupieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_contacts)
        val rv : RecyclerView = findViewById(R.id.contactsRv)
        rv.adapter = adapter
        fetchUsers()
    }

    fun fetchUsers() {
        FirebaseFirestore.getInstance().collection("users")
            .addSnapshotListener(EventListener { e, error ->
                val docs = e?.documents
                docs?.forEach { doc ->
                    val u = doc.toObject(User::class.java)
                    u?.let {
                        adapter.add(ContactItem(it.email, it.uriProfile))
                    }
                }
            })
    }

    private class ContactItem(val email: String, val image: String) : Item<GroupieViewHolder>() {

        override fun getLayout() = R.layout.item_contact

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val txEmail = viewHolder.itemView.findViewById<TextView>(R.id.txt_email)
            val imgPhoto = viewHolder.itemView.findViewById<ImageView>(R.id.imageView)

            Picasso.get()
                    .load(image)
                    .into(imgPhoto)
            txEmail.text = email;
        }
    }
}
