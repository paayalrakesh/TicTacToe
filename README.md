# ❌⭕ X and O - Android Tic Tac Toe Game

An Android implementation of the classic Tic Tac Toe (X and O) game using Kotlin and XML layouts. The current version supports local multiplayer (two players on the same device). This app serves as a foundation to extend into **solo with AI** and **online multiplayer**.

---

## 🎮 Features

* Classic 3x3 grid game
* Local 2-player mode
* Reset functionality
* Simple and clean UI using GridLayout
* Toasts for player turns and results

---

## 🚀 How to Run Locally

### 📥 Steps

1. Clone the repository from GitHub.
2. Open the project in Android Studio.
3. Connect a physical device or use an emulator.
4. Click the **Run** button.

---

## 🌐 Making the App Online Multiplayer

To support **online play** between two users, follow these steps:

### 🧩 1. Add Firebase to Your Project

* Visit the Firebase Console at [https://console.firebase.google.com/](https://console.firebase.google.com/)
* Create a new Firebase project
* Connect your app to firebase using the following services
* Enable Firebase services:

  * Firebase Realtime Database or Firestore
  * Firebase Authentication (for usernames or anonymous login) - not necessary, can ask user to input username on landing page

Ensure your Gradle files include Firebase services.

### 🌍 2. Implement Online Match Logic

* When user selects **Online Match** from the landing page:

  * Create or join a game session (game room)
  * Use Firebase to store the game board state and player turns under a `games/{roomId}` node
  * Update the board in real-time as players make moves
  * Show opponent's name on screen

#### Example game state (conceptual):

games:
• room123
 • player1: Alice
 • player2: Bob
 • board: \["X", "", "O", "", "", "", "", "", ""]
 • turn: Bob
 • result: ""

---

## 🤖 Solo Mode with Minimax AI

To enable solo gameplay:

1. Add a landing page with mode selection: Solo vs Multiplayer
2. When Solo is selected:

   * Let the user go first
   * On each turn, run the Minimax algorithm to determine the best move for the AI
   * This is a challenege, please use online resources to help
3. Insert the AI’s move automatically after the player’s turn

AI should play as "O" and consider all board states recursively to determine the optimal outcome (win/draw).

---

## 🧠 Code Structure

* MainActivity.kt: Contains all the game logic and board interactions
* activity\_main.xml: The layout with a 3x3 GridLayout and reset button

---

## 💡 Activity Summerized

* Firebase real-time multiplayer
* Minimax AI for single-player mode
* Username and profile management
* Firebase leaderboards - later on
* Animations and UI polish
* Sound effects

---

---

Let me know if you'd like me to generate the Firebase multiplayer game logic next, or the AI logic for solo mode.
