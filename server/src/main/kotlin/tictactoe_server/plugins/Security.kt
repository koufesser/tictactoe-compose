package tictactoeserver.plugins

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

data class Connection(val session: DefaultWebSocketSession) {
    var stopped = false
    val name = "user${lastId.getAndIncrement()}"
    companion object {
        val lastId = AtomicInteger(0)
    }
}
