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

class SoloActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var resetButton: Button
    private var gameState = Array(9) { "" }
    private var playerTurn = true // true = Player (X), false = AI (O)
    private var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_solo)

        gridLayout = findViewById(R.id.gridLayout)
        resetButton = findViewById(R.id.resetButton)

        initializeBoard()

        resetButton.setOnClickListener {
            initializeBoard()
            resetButton.visibility = View.INVISIBLE
            gameState.fill("")
            gameOver = false
            playerTurn = true
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
                setOnClickListener { playGame(this, i) }
            }
            gridLayout.addView(button)
        }
    }

    private fun playGame(button: Button, cellIndex: Int) {
        if (gameOver || gameState[cellIndex].isNotEmpty()) {
            Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show()
            return
        }

        gameState[cellIndex] = "X"
        button.text = "X"
        playerTurn = false

        if (checkForWin()) return

        aiMove()
    }

    private fun aiMove() {
        if (gameOver) return

        val bestMove = minimax(gameState, true).first
        if (bestMove == -1) return

        val button = gridLayout.getChildAt(bestMove) as Button
        gameState[bestMove] = "O"
        button.text = "O"
        playerTurn = true

        checkForWin()
    }

    private fun checkForWin(): Boolean {
        val winningPositions = arrayOf(
            arrayOf(0, 1, 2), arrayOf(3, 4, 5), arrayOf(6, 7, 8),
            arrayOf(0, 3, 6), arrayOf(1, 4, 7), arrayOf(2, 5, 8),
            arrayOf(0, 4, 8), arrayOf(2, 4, 6)
        )

        for (pos in winningPositions) {
            if (gameState[pos[0]] == gameState[pos[1]] &&
                gameState[pos[1]] == gameState[pos[2]] &&
                gameState[pos[0]] != "") {
                showToast("${gameState[pos[0]]} has won")
                gameOver = true
                resetButton.visibility = View.VISIBLE
                return true
            }
        }

        if (gameState.none { it.isEmpty() }) {
            showToast("It's a draw")
            gameOver = true
            resetButton.visibility = View.VISIBLE
            return true
        }

        return false
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun minimax(state: Array<String>, isMaximizing: Boolean): Pair<Int, Int> {
        //pulls the game state (array)
        val availableMoves = state.indices.filter { state[it] == "" }

        if (isWinner(state, "X")) return Pair(-1, -10)
        if (isWinner(state, "O")) return Pair(-1, 10)
        if (availableMoves.isEmpty()) return Pair(-1, 0)

        var bestMove = -1
        //is maximizing determines the best move (if theres a block next to it whihc is empty)
        var bestScore = if (isMaximizing) Int.MIN_VALUE else Int.MAX_VALUE
        //min value is closest block, max value would be the next closest block not filled in the array
        for (move in availableMoves) {
            state[move] = if (isMaximizing) "O" else "X"
            val score = minimax(state, !isMaximizing).second
            state[move] = ""
            //sets the score to the lowest
            if (isMaximizing) {
                if (score > bestScore) {
                    bestScore = score
                    bestMove = move
                }
                //sets it further away
            } else {
                if (score < bestScore) {
                    bestScore = score
                    bestMove = move
                }
            }
        }
        //NOTE - this doesnt simulate playing on the board even if it may look like it, its just analysing the array
        // you will notice it moves to the next index in the array instead of moving down a row
        // this works fine to simulate another player, however it does not pose any difficulty
        return Pair(bestMove, bestScore)
    }

    //checks for winner based on player moves/ ai moves across pre defined array sequences
    private fun isWinner(state: Array<String>, player: String): Boolean {
        val winningPositions = arrayOf(
            arrayOf(0, 1, 2), arrayOf(3, 4, 5), arrayOf(6, 7, 8),
            arrayOf(0, 3, 6), arrayOf(1, 4, 7), arrayOf(2, 5, 8),
            arrayOf(0, 4, 8), arrayOf(2, 4, 6)
        )
        return winningPositions.any { pos ->
            state[pos[0]] == player && state[pos[1]] == player && state[pos[2]] == player
        }
    }
}