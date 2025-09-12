package com.example.xando

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OnlineActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var gridLayout: GridLayout
    private lateinit var resetButton: Button
    private var gameId = "game123" // You can generate or fetch this dynamically
    private var playerSymbol = "X"
    private var opponentSymbol = "O"
    private var isPlayerTurn = true
    private var gameState = Array(9) { "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online)

        gridLayout = findViewById(R.id.gridLayout)
        resetButton = findViewById(R.id.resetButton)
        dbRef = FirebaseDatabase.getInstance().reference

        initializeBoard()
        listenToBoard()

        resetButton.setOnClickListener {
            resetGame()
        }
    }

    private fun initializeBoard() {
        gridLayout.removeAllViews()
        for (i in 0 until 9) {
            val button = Button(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    rowSpec = GridLayout.spec(i / 3, 1f)
                    columnSpec = GridLayout.spec(i % 3, 1f)
                }
                text = ""
                setOnClickListener {
                    if (isPlayerTurn && text == "") {
                        makeMove(i)
                    }
                }
            }
            gridLayout.addView(button)
        }
    }

    private fun makeMove(index: Int) {
        gameState[index] = playerSymbol
        updateFirebaseBoard()
    }

    private fun updateFirebaseBoard() {
        dbRef.child("games").child(gameId).child("board").setValue(gameState.toList())
        dbRef.child("games").child(gameId).child("turn").setValue(opponentSymbol)
    }

    private fun listenToBoard() {
        dbRef.child("games").child(gameId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val board = snapshot.child("board").getValue(List::class.java)
                val turn = snapshot.child("turn").getValue(String::class.java)
                if (board != null && board.size == 9) {
                    for (i in board.indices) {
                        gameState[i] = (board[i] ?: "").toString()
                        val button = gridLayout.getChildAt(i) as Button
                        button.text = gameState[i]
                    }
                }
                isPlayerTurn = (turn == playerSymbol)
                checkForWin()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@OnlineActivity, "Database error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkForWin() {
        val winningPositions = arrayOf(
            arrayOf(0, 1, 2), arrayOf(3, 4, 5), arrayOf(6, 7, 8),
            arrayOf(0, 3, 6), arrayOf(1, 4, 7), arrayOf(2, 5, 8),
            arrayOf(0, 4, 8), arrayOf(2, 4, 6)
        )
        winningPositions.forEach { pos ->
            if (gameState[pos[0]] == gameState[pos[1]] &&
                gameState[pos[1]] == gameState[pos[2]] &&
                gameState[pos[0]] != ""
            ) {
                Toast.makeText(this, "${gameState[pos[0]]} wins!", Toast.LENGTH_LONG).show()
                resetButton.visibility = View.VISIBLE
                return
            }
        }
        if (gameState.none { it.isEmpty() }) {
            Toast.makeText(this, "It's a draw!", Toast.LENGTH_LONG).show()
            resetButton.visibility = View.VISIBLE
        }
    }

    private fun resetGame() {
        gameState.fill("")
        dbRef.child("games").child(gameId).child("board").setValue(gameState.toList())
        dbRef.child("games").child(gameId).child("turn").setValue("X")
        resetButton.visibility = View.INVISIBLE
    }
}