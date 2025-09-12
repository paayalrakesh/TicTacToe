package com.example.xando

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    private lateinit var offlineBtn : Button
    private lateinit var soloBtn : Button
    private lateinit var onlineBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        offlineBtn = findViewById(R.id.OfflineButton)
        soloBtn = findViewById(R.id.SoloButton)
        onlineBtn = findViewById(R.id.OnlineButton)

        offlineBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        soloBtn.setOnClickListener {
            startActivity(Intent(this, SoloActivity::class.java))
        }
        onlineBtn.setOnClickListener {
            startActivity(Intent(this, SoloActivity::class.java))
        }
    }
}