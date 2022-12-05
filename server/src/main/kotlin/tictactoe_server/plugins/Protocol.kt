package tictactoe_server.plugins

import kotlinx.serialization.Serializable

@Serializable
data class Answer(
    val check: Boolean,
    val table: MutableList<KeyType> = MutableList(9) {KeyType.EMPTY},
    val turn: KeyType = KeyType.EMPTY,
    val player: KeyType = KeyType.EMPTY,
    val text: String = "Hi",
    val winner: KeyType = KeyType.EMPTY
)

@Serializable
data class Msg(val command: Command, val text : String = "", val button: Int = 0)

enum class Command {
    START, UPDATE, PRESS, STOP
}
