package com.example.mechanicapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.mechanicapp.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference

class IntroActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnProceedAct = findViewById<ImageButton>(R.id.btnProceed)
        btnProceedAct.setOnClickListener{
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

    }
}