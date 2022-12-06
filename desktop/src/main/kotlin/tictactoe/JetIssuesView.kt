package tictactoe

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import kotlin.concurrent.thread

@Composable
fun JetIssuesView() {
    MaterialTheme(
        colors = lightThemeColors
    ) {
        Main()
    }
}

@Composable
@Preview
fun Main() {
    val game = remember { Game() }
    Column(Modifier.fillMaxHeight()) {
            Button(onClick = {
                when (game.gameStatus) {

                    GameStatus.START -> {
                        println("[[CLIENT]] status START -> PLAYING because the button was pressed")
                        game.gameStatus = GameStatus.PLAYING
                        thread(start = true) {
                            game.start()
                        }
                    }

                    GameStatus.PLAYING -> {
                        println("[[CLIENT]] status PLAYING -> FINISHED because the button was pressed")
                        game.gameStatus = GameStatus.FINISHED
                    }

                    GameStatus.FINISHED -> {
                        println("[[CLIENT]] status FINISHED -> START because the button was pressed")
                        game.text = "Press start to play"
                        game.gameStatus = GameStatus.START
                    }
                }
            }) {
                Text(
                    when (game.gameStatus) {
                        GameStatus.START -> "Start"
                        GameStatus.PLAYING -> "Resign"
                        GameStatus.FINISHED -> "Return"
                    }, fontSize = 40.sp
                )
            }
        DisplayPanel(
            Modifier.weight(1f),
            game
        )

        if (game.gameStatus == GameStatus.PLAYING && game.turn != KeyType.EMPTY ) {
            Text("Turn: ${Key.value(game.turn)}", fontSize = 40.sp)
            Text("Your type: ${Key.value(game.player)}", fontSize = 40.sp)
        }

        if (game.gameStatus == GameStatus.PLAYING) {
            keyboard(
                Modifier.weight(4f),
                game
            )
        }
    }
}

val lightThemeColors = lightColors(
    primary = Color(0xFFDD0D3C),
    primaryVariant = Color(0xFFC20029),
    secondary = Color.White,
    error = Color(0xFFD00036)
)


