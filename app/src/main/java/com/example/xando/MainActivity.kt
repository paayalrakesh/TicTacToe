package com.example.xando

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var resetButton: Button

    // true = Player 1 (X), false = Player 2 (O)
    private var playerTurn = true
    private var gameState = Array(9) { "" }
    private var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)
        resetButton = findViewById(R.id.resetButton)

        initializeBoard()
        Toast.makeText(this, "Player 1's turn (X)", Toast.LENGTH_SHORT).show()

        resetButton.setOnClickListener {
            // reset state
            gameState.fill("")
            gameOver = false
            playerTurn = true
            // rebuild buttons (re-enables all)
            initializeBoard()
            resetButton.visibility = View.INVISIBLE
            Toast.makeText(this, "Player 1's turn (X)", Toast.LENGTH_SHORT).show()
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
                textSize = 24f
                isAllCaps = false
                isEnabled = true
                setOnClickListener { onCellClicked(this, i) }
            }
            gridLayout.addView(button)
        }
    }

    private fun onCellClicked(button: Button, index: Int) {
        if (gameOver) return
        if (gameState[index].isNotEmpty()) {
            Toast.makeText(this, "Cell is already occupied", Toast.LENGTH_SHORT).show()
            return
        }

        val symbol = if (playerTurn) "X" else "O"
        gameState[index] = symbol
        button.text = symbol

        // Check for win/draw caused by this move
        if (checkForEnd()) return

        // Next player's turn
        playerTurn = !playerTurn
        if (playerTurn) {
            Toast.makeText(this, "Player 1's turn (X)", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Player 2's turn (O)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkForEnd(): Boolean {
        val wins = arrayOf(
            intArrayOf(0, 1, 2), intArrayOf(3, 4, 5), intArrayOf(6, 7, 8), // rows
            intArrayOf(0, 3, 6), intArrayOf(1, 4, 7), intArrayOf(2, 5, 8), // cols
            intArrayOf(0, 4, 8), intArrayOf(2, 4, 6)                        // diags
        )

        for (w in wins) {
            val (a, b, c) = w
            if (gameState[a].isNotEmpty() &&
                gameState[a] == gameState[b] &&
                gameState[b] == gameState[c]
            ) {
                Toast.makeText(this, "${gameState[a]} has won", Toast.LENGTH_LONG).show()
                endRound()
                return true
            }
        }

        if (gameState.none { it.isEmpty() }) {
            Toast.makeText(this, "It's a draw", Toast.LENGTH_LONG).show()
            endRound()
            return true
        }

        return false
    }

    private fun endRound() {
        gameOver = true
        // disable all cells so no more taps register
        for (i in 0 until gridLayout.childCount) {
            gridLayout.getChildAt(i).isEnabled = false
        }
        resetButton.visibility = View.VISIBLE
    }
}
