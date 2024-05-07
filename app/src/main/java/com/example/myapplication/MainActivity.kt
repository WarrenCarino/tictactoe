package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

//for saving the result of the game
data class TicTacToeGameState(
    var board: List<TicTacToeCell> = List(9) { TicTacToeCell.Empty },
    var currentPlayer: TicTacToePlayer = TicTacToePlayer.X,
    var gameResult: TicTacToeGameResult = TicTacToeGameResult.InProgress,
    var playerXName: String = "Player X",
    var playerOName: String = "Player O",
    var playerXScore: Int = 0,
    var playerOScore: Int = 0
)

@Composable
fun TicTacToeGame() {
    var gameState by remember { mutableStateOf(TicTacToeGameState()) }
    val backgroundImagePainter: Painter = painterResource(id = R.drawable.aesthetic)

    Image(
        painter = backgroundImagePainter,
        contentDescription = "Background Image",
        modifier = Modifier.fillMaxSize()

    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tic-Tac-Toe", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            NameInput(
                label = "Player X",
                value = gameState.playerXName,
                onValueChange = { gameState = gameState.copy(playerXName = it) }
            )
            Spacer(modifier = Modifier.width(32.dp))
            NameInput(
                label = "Player O",
                value = gameState.playerOName,
                onValueChange = { gameState = gameState.copy(playerOName = it) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        GridFor(gameState.board) { index ->
            if (gameState.gameResult == TicTacToeGameResult.InProgress && gameState.board[index] == TicTacToeCell.Empty) {
                gameState = gameState.copy(
                    board = gameState.board.toMutableList().also { it[index] = gameState.currentPlayer.cell },
                    gameResult = checkGameResult(gameState.board)
                )

                if (gameState.gameResult == TicTacToeGameResult.PlayerXWins) {
                    gameState = gameState.copy(playerXScore = gameState.playerXScore + 1)
                } else if (gameState.gameResult == TicTacToeGameResult.PlayerOWins) {
                    gameState = gameState.copy(playerOScore = gameState.playerOScore + 1)
                }

                gameState = gameState.copy(
                    currentPlayer = if (gameState.currentPlayer == TicTacToePlayer.X) TicTacToePlayer.O else TicTacToePlayer.X
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            gameState = TicTacToeGameState(
                playerXName = gameState.playerXName,
                playerOName = gameState.playerOName
            )
        }) {
            Text("Restart Game")
        }
//para magprint kung panalo
        if (gameState.gameResult != TicTacToeGameResult.InProgress) {
            val resultText = when (gameState.gameResult) {
                TicTacToeGameResult.PlayerXWins -> "${gameState.playerXName} wins!"
                TicTacToeGameResult.PlayerOWins -> "${gameState.playerOName} wins!"
                TicTacToeGameResult.Draw -> "It's a draw!"
                else -> ""
            }
            Text(resultText, style = MaterialTheme.typography.headlineLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("${gameState.playerXName} Score: ${gameState.playerXScore}")
        Text("${gameState.playerOName} Score: ${gameState.playerOScore}")
    }
}

@Composable
fun NameInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(text = label)
        TextField(value = value, onValueChange = onValueChange)
    }
}
//loopings
@Composable
fun GridFor(board: List<TicTacToeCell>, onCellClicked: (Int) -> Unit) {
    Column {
        for (row in 0 until 3) {
            Row {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    TicTacToeCellButton(cell = board[index], onClick = { onCellClicked(index) })
                }
            }
        }
    }
}
//para maclick yung box
@Composable
fun TicTacToeCellButton(cell: TicTacToeCell, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = when (cell) {
                TicTacToeCell.X -> "X"
                TicTacToeCell.O -> "O"
                else -> ""
            },
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )
    }
}
//para lumabas yung x and 0 pagpinindot yung box
enum class TicTacToePlayer(val cell: TicTacToeCell) {
    X(TicTacToeCell.X),
    O(TicTacToeCell.O)
}
//para maiwasan ang empty value
enum class TicTacToeCell {
    X, O, Empty
}
// para malaman kung sino mananalo
enum class TicTacToeGameResult {
    InProgress, PlayerXWins, PlayerOWins, Draw
}

//pattern for this tictactoe
fun checkGameResult(board: List<TicTacToeCell>): TicTacToeGameResult {
    val winPatterns = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Horizontal
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Vertical
        listOf(0, 4, 8), listOf(2, 4, 6) // Diagonal
    )
    for (pattern in winPatterns) {
        val cells = pattern.map { board[it] }
        if (cells.all { it == TicTacToeCell.X }) {
            return TicTacToeGameResult.PlayerXWins
        }
        if (cells.all { it == TicTacToeCell.O }) {
            return TicTacToeGameResult.PlayerOWins
        }
    }

    if (board.all { it != TicTacToeCell.Empty }) {
        return TicTacToeGameResult.Draw
    }

    return TicTacToeGameResult.InProgress
}



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeGame()
        }
    }
}

