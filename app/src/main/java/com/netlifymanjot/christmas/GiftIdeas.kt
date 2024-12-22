package com.netlifymanjot.christmas

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.*
import com.netlifymanjot.christmas.adapter.GiftAdapter
import com.netlifymanjot.christmas.adapter.GiftData

class GiftIdeas : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: GiftAdapter
    private val databaseRef = FirebaseDatabase.getInstance().reference.child("giftIdeas")
    private val giftItems = mutableListOf<GiftData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gift_ideas)

        viewPager = findViewById(R.id.viewPager)
        adapter = GiftAdapter(giftItems) { url ->
            openAffiliateLink(url)
        }
        viewPager.adapter = adapter

        fetchGiftIdeas()
    }

    private fun fetchGiftIdeas() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                giftItems.clear()
                for (child in snapshot.children) {
                    val name = child.child("name").value as? String ?: ""
                    val url = child.child("url").value as? String ?: ""
                    val imageUrl = child.child("imageUrl").value as? String ?: ""
                    val description = child.child("description").value as? String ?: ""

                    if (name.isNotEmpty() && url.isNotEmpty() && imageUrl.isNotEmpty() && description.isNotEmpty()) {
                        giftItems.add(GiftData(name, url, imageUrl, description))
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GiftIdeas, "Failed to load gift ideas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun openAffiliateLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}
