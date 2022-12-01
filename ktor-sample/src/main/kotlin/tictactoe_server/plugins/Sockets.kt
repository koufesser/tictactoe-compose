package tictactoe_server.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Thread.sleep
import java.time.Duration
import java.util.*
import kotlin.concurrent.thread

object Sockets {
    var waitingConnection: Connection? = null
    val connections = Collections.synchronizedMap<Connection, Game>(LinkedHashMap())

    @Synchronized
    fun getEnemy(connection: Connection) {
        if (waitingConnection == null) {
            waitingConnection = connection
            return
        }
        val enemy = waitingConnection
        val game = Game(enemy!!, connection)
        connections[connection] = game
        connections[enemy] = game
        waitingConnection = null
    }

    @Synchronized
    fun deleteConnection(connection: Connection): Boolean {
        if (waitingConnection == connection) {
            waitingConnection = null
            return true
        }
        return false
    }

    fun removeConnection(connection: Connection) {
        connections -= connection
    }
}

fun sanityCheck() {

}

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/chat") {
            var game: Game? = null
            val thisConnection = Connection(this)
            println("${thisConnection.name}  Adding user!")
            sendMessage("Looking for enemy", thisConnection)

            Sockets.getEnemy(thisConnection)
            thread {
                while (game == null && !thisConnection.stopped) {
                    if (Sockets.connections.containsKey(thisConnection)) {
                        game = Sockets.connections[thisConnection]
                        runBlocking {
                            sendMessage("Game starts here!", thisConnection, game)
                        }
                        println("${thisConnection.name} FOUND game")
                    }
                }
                if (thisConnection.stopped || game == null) {
                    println("${thisConnection.name}  [thread 1]  GAME STOPPED")
                    thisConnection.stopped = true
                }
            }

            try {
                for (frame in incoming) {
                    if (thisConnection.stopped)
                        break
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    println("${thisConnection.name}  [THREAD 1] got message: $receivedText")
                    val msg: Msg = Json.decodeFromString(receivedText)
                    when (msg.command) {
                        Command.PRESS -> processPress(msg, game, thisConnection)
                        Command.START -> processPress(msg, game, thisConnection)
                        Command.UPDATE -> TODO()
                        Command.STOP -> {
                            thisConnection.stopped = true
//                            sendEndingMessages("got command STOP", game = game, connection = thisConnection)
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
                println("${thisConnection.name} [THREAD 1] caught exception")
                try {
//                    sendEndingMessages("Enemy resigned", game, thisConnection)
                } catch (_: Exception) {
                }
            } finally {
                println("Removing $thisConnection!")
                Sockets.removeConnection(thisConnection)
            }
        }
    }
}

suspend fun sendMessage(text: String, connection: Connection, game: Game? = null) {
    println("[[sending message]] ${connection.name} ----- $text")
    val data: Answer = if (game != null) {
        Answer(true, game.table, game.turn, game.getType(connection), text)
    } else {
        Answer(true, text = text)
    }
    connection.session.send(Json.encodeToString(data))
}

suspend fun sendMessageToEnemy(text: String, connection: Connection, game: Game) {
    println("[[sending msg to enemy]] ${game.getEnemy(connection).name} ---- $text")
    val data = Answer(true, game.table, game.turn, game.getType(game.getEnemy(connection)), text)
    game.getEnemy(connection).session.send(Json.encodeToString(data))
}

suspend fun sendEndingMessages(text: String, game: Game, connection: Connection) {
    println("[[sending ending messages]] ${connection.name} ---- $text")

    val data = Answer(false, text = text)

    try {
        connection.session.send(Json.encodeToString(data))
    } catch (_: Exception) {
    }
    try {
        game.getEnemy(connection).session.send(Json.encodeToString(data))
    } catch (_: Exception) {
    }
}

suspend fun processPress(msg: Msg, game: Game?, connection: Connection) {
    if (game == null) {
        sendMessage("Looking for enemy", connection)
        return
    }

    val position = msg.button
    if (position > 9 || position < 0) {
        sendMessage("Wrong command", connection = connection, game = game)
    }

    if (game.setValue(connection, position)) {
        sendMessage("Counted", connection = connection, game = game)
        sendMessageToEnemy("Your turn!", connection = connection, game = game)
    } else {
        if (game.turn != game.getType(connection))
            sendMessage("Not your turn!", connection = connection, game = game)
        else {
            sendMessage("Cell is not empty!", connection = connection, game = game)
        }
    }

    if (game.winner != KeyType.EMPTY) {
        sendEndingMessages("Game ended! The winner is ${game.winner}", connection = connection, game = game)
        connection.stopped = true
        game.getEnemy(connection).stopped = true
    }
}