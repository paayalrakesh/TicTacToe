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

        onlineBtn.setOnClickListener {
            // quick Host/Join chooser with a room code prompt when joining
            val options = arrayOf("Host game (you are X)", "Join game (you are O)")
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Online Multiplayer")
                .setItems(options) { _, which ->
                    if (which == 0) {
                        val room = (100000..999999).random().toString()
                        startActivity(
                            Intent(this, OnlineActivity::class.java)
                                .putExtra("mode", "host")
                                .putExtra("room", room)
                        )
                    } else {
                        // prompt for a room code
                        val input = android.widget.EditText(this).apply {
                            hint = "Enter 6-digit room code"
                            inputType = android.text.InputType.TYPE_CLASS_NUMBER
                        }
                        androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Join Room")
                            .setView(input)
                            .setPositiveButton("Join") { _, _ ->
                                val room = input.text.toString().trim()
                                if (room.length >= 3) {
                                    startActivity(
                                        Intent(this, OnlineActivity::class.java)
                                            .putExtra("mode", "join")
                                            .putExtra("room", room)
                                    )
                                } else {
                                    android.widget.Toast.makeText(this, "Invalid code", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
                .show()
        }


        soloBtn.setOnClickListener {
            startActivity(Intent(this, SoloActivity::class.java))
        }
        onlineBtn.setOnClickListener {
            startActivity(Intent(this, SoloActivity::class.java))
        }
    }
}