package tictactoe_server.plugins

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }
    var stopped = false
    val name = "user${lastId.getAndIncrement()}"
}
