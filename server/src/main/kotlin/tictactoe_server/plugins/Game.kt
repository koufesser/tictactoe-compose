package tictactoeserver.plugins

class Game(val O: Connection, val X: Connection) {
    var turn: KeyType = KeyType.O
    val table = mutableListOf<KeyType>()
    var winner: KeyType = KeyType.EMPTY

    init {
        repeat(9) {
            table.add(KeyType.EMPTY)
        }
    }

    fun getType(connection: Connection): KeyType {
        if (O == connection)
            return KeyType.O
        if (X == connection)
            return KeyType.X
        assert(false) { "wrong table" }
        return KeyType.EMPTY
    }

    private fun changeTurn() {
        turn = if (turn == KeyType.X)
            KeyType.O
        else KeyType.X
    }

    @Synchronized
    @Suppress("FUNCTION_BOOLEAN_PREFIX")
    fun setValue(connection: Connection, position: Int): Boolean {
        if (position < 0 || position > 8)
            return false
        val type = getType(connection)
        if (turn != type)
            return false
        if (table[position] != KeyType.EMPTY)
            return false
        table[position] = type
        changeTurn()
        checkWin()
        return true
    }

    fun getEnemy(connection: Connection): Connection {
        if (getType(connection) == KeyType.X)
            return O
        return X
    }

    @Synchronized
    fun checkWin() {
        if (!table.any { it == KeyType.EMPTY }) {
            winner = KeyType.DRAW
            return
        }
        for (d in directions) {
            var check = true
            var type = KeyType.EMPTY
            for (p in d) {
                if (type == KeyType.EMPTY)
                    type = table[p.digitToInt()]
                if (table[p.digitToInt()] == KeyType.EMPTY || type != table[p.digitToInt()]) {
                    check = false
                    break
                }
            }
            if (check)
                winner = type
        }
    }

  companion object {
      fun emptyTable() = MutableList(9) { KeyType.EMPTY }
  }
}

@Suppress("CONFUSING_IDENTIFIER_NAMING")
enum class KeyType {
    DRAW, EMPTY, O, X
}

val directions = listOf("012", "345", "678", "036", "147", "258", "048", "246")
