package com.netlifymanjot.christmas

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.*
import com.netlifymanjot.christmas.adapter.ImageAdapter
import com.netlifymanjot.christmas.adapter.ImageData
import com.netlifymanjot.christmas.databinding.ActivityExploreBinding

class Explore : AppCompatActivity() {

    private lateinit var binding: ActivityExploreBinding

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ImageAdapter
    private val databaseRef = FirebaseDatabase.getInstance().reference.child("images")
    private val images = mutableListOf<ImageData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        binding = ActivityExploreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = findViewById(R.id.viewPager)
        adapter = ImageAdapter(images)
        viewPager.adapter = adapter

        fetchImages()

        binding.fabUpload.setOnClickListener{
            val intent = Intent(this, Upload::class.java)
            startActivity(intent)
        }
    }

    private fun fetchImages() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                images.clear()
                for (child in snapshot.children) {
                    val name = child.child("name").value as? String ?: ""
                    val url = child.child("url").value as? String ?: ""
                    if (name.isNotEmpty() && url.isNotEmpty()) {
                        images.add(ImageData(name, url))
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Explore, "Failed to load images: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
