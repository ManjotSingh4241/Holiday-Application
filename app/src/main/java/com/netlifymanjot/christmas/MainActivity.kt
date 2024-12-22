package com.netlifymanjot.christmas

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.netlifymanjot.christmas.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.explore.setOnClickListener{
            val intent = Intent(this, Explore::class.java)
            startActivity(intent)
        }
        binding.ideas.setOnClickListener{
            val intent = Intent(this, GiftIdeas::class.java)
            startActivity(intent)
        }
        binding.createButton.setOnClickListener{
            val intent = Intent(this, CreateCards::class.java)
            startActivity(intent)
        }
    }
}