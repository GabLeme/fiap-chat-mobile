package com.fiap

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fiap.entity.Contact
import com.fiap.entity.Message
import com.fiap.entity.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ChatActivity : AppCompatActivity() {

    val adapter = GroupieAdapter()
    lateinit var edtChat: EditText;
    var me : User? = User()
    public var user: User? = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_chat)
        user = intent.getParcelableExtra<User>("user")

        user?.let {
            supportActionBar?.title = user!!.email
        }
        val rv : RecyclerView = findViewById(R.id.chatRv)
        rv.adapter = adapter


        FirebaseAuth.getInstance().uid?.let { it ->
            FirebaseFirestore.getInstance().collection("/users")
                .document(it)
                .get()
                .addOnSuccessListener(OnSuccessListener {
                    me = it.toObject(User::class.java)
                    Log.i("me", me!!.uuid)
                    fetchMessages()
                })
        }

        val btnChat : Button = findViewById(R.id.btnChat)
        edtChat = findViewById(R.id.edtChat)

        btnChat.setOnClickListener(View.OnClickListener {
            sendMessage()
        })
    }

    fun fetchMessages() {
        me?.let {
            val fromId = it.uuid
            val toId = user!!.uuid
            FirebaseFirestore.getInstance().collection("/conversations")
                .document(fromId)
                .collection(toId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, firebaseFirestoreException: FirebaseFirestoreException? ->
                    val docChanges = querySnapshot?.documentChanges
                    docChanges.let {
                        Log.i("toaq", it.toString())
                        it!!.forEach {
                            if(it.type == DocumentChange.Type.ADDED) {
                                Log.i("doc", it.document.toString())
                                adapter.add(MessageItem(it.document.toObject(Message::class.java)))
                            }
                        }
                    }
                }

        }
    }


    fun sendMessage()  {
        val txt = edtChat.text.toString();
        edtChat.text = null
        val fromId = FirebaseAuth.getInstance().uid!!
        val toId = user!!.uuid
        val timestamp = System.currentTimeMillis()
        val msg = Message()
        msg.fromId = fromId
        msg.toId = toId
        msg.text = txt
        msg.timestamp = timestamp

        Log.i("idfrom", fromId)
        Log.i("toId", toId)
        if(msg.text.isNotEmpty()) {
            FirebaseFirestore.getInstance().collection("/conversations")
                .document(fromId)
                .collection(toId)
                .add(msg)
                .addOnSuccessListener(OnSuccessListener {
                    FirebaseFirestore.getInstance().collection("/last-messages")
                            .document(fromId)
                            .collection("contacts")
                            .document(toId)
                            .set(Contact(
                                    toId,
                                    user!!.email,
                                    msg.text,
                                    msg.timestamp,
                                    user!!.uriProfile
                            ))

                })

            FirebaseFirestore.getInstance().collection("/conversations")
                .document(toId)
                .collection(fromId)
                .add(msg)
                .addOnSuccessListener(OnSuccessListener {
                    FirebaseFirestore.getInstance().collection("/last-messages")
                            .document(toId)
                            .collection("contacts")
                            .document(fromId)
                            .set(Contact(
                                    toId,
                                    user!!.email,
                                    msg.text,
                                    msg.timestamp,
                                    user!!.uriProfile
                            ))
                })
        }
    }

    inner private class MessageItem(val message: Message) : Item<GroupieViewHolder>() {

        override fun getLayout(): Int {
            return when(message.fromId) {
                FirebaseAuth.getInstance().uid -> R.layout.item_from_message
                else -> R.layout.item_to_message
            }
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val txtMessage = viewHolder.itemView.findViewById<TextView>(R.id.edtChat)
            val imgPhoto = viewHolder.itemView.findViewById<ImageView>(R.id.imgMessageUser)

            txtMessage.text = message.text
            Picasso.get()
                .load(user!!.uriProfile)
                .into(imgPhoto)
        }
    }
}