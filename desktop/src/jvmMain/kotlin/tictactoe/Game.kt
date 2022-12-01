package tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.*

class Game {
    var table by mutableStateOf(emptyTable())
    var text by mutableStateOf("")
    val msgs = mutableListOf<Msg>()

    @Synchronized
    fun addMsg(msg : Msg) {
        println(msg)
        msgs.add(msg)
    }
    var gameStatus by mutableStateOf(GameStatus.START)
    var turn by mutableStateOf(KeyType.EMPTY)
    val client = HttpClient(CIO) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }
    var player by mutableStateOf(KeyType.EMPTY)

    private fun processAnswer(msgText: String) {
        val answer: Answer = Json.decodeFromString(msgText)
        for (i in (0..8) ) {
            table[i/3][i % 3].type = answer.table[i]
        }
        if (!answer.check) {
            println("[[CLIENT]] status finished because server sent finish")
            gameStatus = GameStatus.FINISHED
        }
        text = answer.text
        turn = answer.turn
        player = answer.player
    }

    fun start() {
        addMsg(Msg(Command.START, "starting"))
        runBlocking {
            try {
                client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/chat") {
                    while (gameStatus == GameStatus.PLAYING) {
                        if (msgs.isNotEmpty()) {
                            println("[[CLIENT]] sending message ---- ${msgs[0]}")
                            sendSerialized(msgs[0])
                            msgs.removeAt(0)
                        }
                        val res = incoming.tryReceive()
                        if (res.isSuccess) {
                            println("[SERVER]: ${(res.getOrNull()!! as Frame.Text).readText()}")
                            processAnswer((res.getOrNull()!! as Frame.Text).readText())
                        }
                    }
                    println("[[CLIENT]] stopping")
                    stop()
                    while (msgs.isNotEmpty()) {
                        println("[[CLIENT]] sending message ---- ${msgs[0]}")
                        sendSerialized(msgs[0])
                        msgs.removeAt(0)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                canNotConnect()
            }
        }
    }

    fun stop() {
        table = emptyTable()

        addMsg(Msg(Command.STOP))
    }

    fun canNotConnect() {
        println("[[CLIENT]] status start because can not connect")
        gameStatus = GameStatus.START
        text = "Can not connect"
    }

    companion object {
        fun emptyTable() = KeyboardLayout
    }
}

enum class GameStatus {
    START, PLAYING, FINISHED
}

enum class Command {
    START, UPDATE, PRESS, STOP
}

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
data class Msg(val command: Command, val text: String = "", val button: Int = 0)
