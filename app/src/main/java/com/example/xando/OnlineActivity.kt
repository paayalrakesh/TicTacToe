package com.example.xando

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.FirebaseApp


class OnlineActivity : AppCompatActivity() {

    private lateinit var dbRef: DatabaseReference
    private lateinit var gridLayout: GridLayout
    private lateinit var resetButton: Button

    private lateinit var gameId: String
    private lateinit var mySymbol: String           // "X" or "O"
    private lateinit var oppSymbol: String          // "O" or "X"
    private var isMyTurn: Boolean = false

    private var gameState = Array(9) { "" }

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online)

        gridLayout = findViewById(R.id.gridLayout)
        resetButton = findViewById(R.id.resetButton)
        dbRef = FirebaseDatabase.getInstance().reference

        if (FirebaseApp.getApps(this).isEmpty()) {
            Toast.makeText(this, "Firebase not initialized", Toast.LENGTH_LONG).show()
            // skip or finish to prevent crashes
        } else {
            Toast.makeText(this, "Firebase OK", Toast.LENGTH_SHORT).show()
        }

        // ---- get mode + room from HomeActivity ----
        val mode = intent.getStringExtra("mode") ?: "host"
        gameId = intent.getStringExtra("room") ?: "000000"

        if (mode == "host") {
            mySymbol = "X"
            oppSymbol = "O"
            isMyTurn = true
            createRoom()
            Toast.makeText(this, "Hosting room $gameId", Toast.LENGTH_SHORT).show()
        } else {
            mySymbol = "O"
            oppSymbol = "X"
            isMyTurn = false
            // join doesn't need to write players for now; just use board/turn
            Toast.makeText(this, "Joined room $gameId", Toast.LENGTH_SHORT).show()
        }

        initializeBoard()
        listenToRoom()

        resetButton.setOnClickListener { resetGame() }
    }

    // Host creates initial state
    private fun createRoom() {
        val init = mapOf(
            "board" to List(9) { "" },
            "turn" to "X",
            "result" to ""
        )
        dbRef.child("games").child(gameId).setValue(init)
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
                    if (text.isNotEmpty()) return@setOnClickListener
                    if (!isMyTurn) {
                        Toast.makeText(this@OnlineActivity, "Wait for your turn ($mySymbol)", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    makeMove(i)
                }
            }
            gridLayout.addView(button)
        }
    }

    private fun makeMove(index: Int) {
        // write the move + advance turn, compute result server-side-ish (here in client then write)
        val nextBoard = gameState.toMutableList()
        nextBoard[index] = mySymbol

        val winner = checkWinner(nextBoard)
        val result = when {
            winner == "X" -> "X"
            winner == "O" -> "O"
            nextBoard.all { it.isNotEmpty() } -> "draw"
            else -> ""
        }
        val nextTurn = if (result.isEmpty()) oppSymbol else mySymbol

        val updates = mapOf(
            "board" to nextBoard,
            "turn" to nextTurn,
            "result" to result
        )
        dbRef.child("games").child(gameId).updateChildren(updates)
    }

    private fun listenToRoom() {
        dbRef.child("games").child(gameId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val board = snapshot.child("board").getValue(object : GenericTypeIndicator<List<String>>() {})
                    val turn = snapshot.child("turn").getValue(String::class.java)
                    val result = snapshot.child("result").getValue(String::class.java)

                    if (board != null && board.size == 9) {
                        // update local state + UI
                        for (i in board.indices) {
                            gameState[i] = board[i]
                            val btn = gridLayout.getChildAt(i) as Button
                            btn.text = board[i]
                            btn.isEnabled = result.isNullOrEmpty() && board[i].isEmpty()
                        }
                    }
                    isMyTurn = (turn == mySymbol)

                    // End state handling
                    when (result) {
                        "X" -> { toast("X wins!"); resetButton.visibility = View.VISIBLE }
                        "O" -> { toast("O wins!"); resetButton.visibility = View.VISIBLE }
                        "draw" -> { toast("It's a draw!"); resetButton.visibility = View.VISIBLE }
                        else -> { /* ongoing */ }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    toast("Database error: ${error.message}")
                }
            })
    }

    private fun resetGame() {
        val map = mapOf(
            "board" to List(9) { "" },
            "turn" to "X",
            "result" to ""
        )
        dbRef.child("games").child(gameId).updateChildren(map)
        resetButton.visibility = View.INVISIBLE
    }

    private fun checkWinner(b: List<String>): String? {
        val w = arrayOf(
            intArrayOf(0,1,2), intArrayOf(3,4,5), intArrayOf(6,7,8),
            intArrayOf(0,3,6), intArrayOf(1,4,7), intArrayOf(2,5,8),
            intArrayOf(0,4,8), intArrayOf(2,4,6)
        )
        for (line in w) {
            val a = b[line[0]]
            if (a.isNotEmpty() && a == b[line[1]] && a == b[line[2]]) return a
        }
        return null
    }

    private fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
