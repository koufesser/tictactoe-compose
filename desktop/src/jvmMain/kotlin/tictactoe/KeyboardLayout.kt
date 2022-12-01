package tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.*

data class Key(
    val position: Int,
    val icon: ImageVector? = null,
    val onClick: ((game : Game) -> Unit)? = null
) {
    var type by mutableStateOf(KeyType.EMPTY)

    companion object{
        fun value(type : KeyType) = when(type) {
            KeyType.EMPTY -> ""
            KeyType.O -> "O"
            KeyType.X -> "X"
        }
    }
}

enum class KeyType {
    EMPTY, X, O
}

fun String.toEmpty() = keyEmpty(this.toInt())

fun keyEmpty(position : Int) = Key(position = position) { game ->
    game.addMsg(
        Msg(Command.PRESS, "Pressed button", position)
    )
}

val KeyboardLayout = listOf(
    mutableListOf("0".toEmpty(), "1".toEmpty(), "2".toEmpty()),
    mutableListOf("3".toEmpty(), "4".toEmpty(), "5".toEmpty()),
    mutableListOf("6".toEmpty(), "7".toEmpty(), "8".toEmpty()),
)
