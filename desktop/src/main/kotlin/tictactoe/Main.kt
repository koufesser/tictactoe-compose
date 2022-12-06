package tictactoe

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.singleWindowApplication

import kotlin.system.exitProcess


fun main() = singleWindowApplication(
    title = "Tic tac toe", state = WindowState(size = DpSize(800.dp, 800.dp))
) {
    JetIssuesView()
}
