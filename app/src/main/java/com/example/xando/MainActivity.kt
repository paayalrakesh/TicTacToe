package com.example.xando

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var resetButton: Button
    private var playerTurn = true // true for Player 1 (X), false for Player 2 (O)
    private var gameState = Array(9) { "" } // "" means the cell/block on the grid is unoccupied

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        gridLayout = findViewById(R.id.gridLayout)
        resetButton= findViewById(R.id.resetButton)

        //sets the board --method below
        initializeBoard()

        //what happens when the reset button is clicked
        resetButton.setOnClickListener {
            //resets the board
            initializeBoard()
            //reset button disappears
            resetButton.visibility = View.INVISIBLE
            Toast.makeText(this, "Player 1's turn (X)",Toast.LENGTH_SHORT).show()
            gameState.fill("")
            playerTurn = true
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
                isEnabled = true // ✅ ensure re-enabled after reset
                setOnClickListener { playGame(this, i) }
            }
            gridLayout.addView(button)
        }
    }


    private fun playGame(button: Button, cellIndex: Int) {
        if (gameState[cellIndex].isNotEmpty()) {
            Toast.makeText(this, "Cell is already occupied", Toast.LENGTH_SHORT).show()
            return
        }
        gameState[cellIndex] = if (playerTurn) "X" else "O"
        button.text = gameState[cellIndex]
        // Switch player turns
        playerTurn = !playerTurn
        checkForWin() // Check if the move leads to a win or a draw -- method below
        if (playerTurn)
        {
            Toast.makeText(this, "Player 1's turn (X)",Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(this, "Player 2's turn (O)",Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkForWin() {
        val winningPositions = arrayOf(
            arrayOf(0, 1, 2), arrayOf(3, 4, 5), arrayOf(6, 7, 8), // Rows
            arrayOf(0, 3, 6), arrayOf(1, 4, 7), arrayOf(2, 5, 8), // Columns
            arrayOf(0, 4, 8), arrayOf(2, 4, 6)                    // Diagonals
        )
        winningPositions.forEach { pos ->
            if (gameState[pos[0]] == gameState[pos[1]] &&
                gameState[pos[1]] == gameState[pos[2]] &&
                gameState[pos[0]] != "") {
                // We have a winner
                showToast("${gameState[pos[0]]} has won")

                for (i in 0 until gridLayout.childCount) {
                    gridLayout.getChildAt(i).isEnabled = false
                }
                resetButton.visibility = View.VISIBLE //brings up the reset button
                return
            }
        }
        // Check for a draw (no empty cells left)
        if (gameState.none { it.isEmpty() }) {
            showToast("It's a draw")

            // 🔒 Disable all cells individually
            for (i in 0 until gridLayout.childCount) {
                gridLayout.getChildAt(i).isEnabled = false
            }

            resetButton.visibility = View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}