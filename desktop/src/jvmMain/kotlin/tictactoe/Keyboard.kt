package tictactoe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun keyboard(
    modifier: Modifier,
    game: Game
    ) {
    Surface(modifier) {
        keyboardKeys(game)
    }
}

@Composable
fun keyboardKeys(game: Game) {
    Row(modifier = Modifier.fillMaxSize()) {
        game.table.forEach { keyColumn ->
            Column(modifier = Modifier.weight(1f)) {
                keyColumn.forEach { key ->
                    keyboardKey(Modifier.weight(1f), key, game)
                }
            }
        }
    }
}

@Composable
fun keyboardKey(modifier: Modifier, key: Key?, game: Game) {
    if (key == null) {
        return emptyKeyView(modifier)
    }
    keyView(modifier = modifier.padding(1.dp), onClick = key.onClick?.let {
        { it(game) }
    } ?: {
        game.msgs.add(Msg(Command.PRESS, "", key.position))
    }) {
        if (key.icon == null) {
            val textStyle =
                TextStyle(
                    fontSize = 29.sp
                )

            Text(
                text = Key.value(key.type),
                style = textStyle
            )
        } else {
            Icon(
                key.icon, null
            )
        }
    }
}

val KEY_BORDER_WIDTH = 1.dp
val KEY_BORDER_COLOR = Color.Gray
val KEY_ACTIVE_BACKGROUND = Color.White
val CALCULATOR_PADDING = 4.dp


@Composable
fun keyView(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    children: @Composable ColumnScope.() -> Unit
) {
    val active = remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
            .padding(CALCULATOR_PADDING)
            .clickable(onClick = onClick)
            .background(color = if (active.value) KEY_ACTIVE_BACKGROUND else MaterialTheme.colors.background)
            .border(width = KEY_BORDER_WIDTH, color = KEY_BORDER_COLOR),
        content = children
    )
}

@Composable
fun emptyKeyView(modifier: Modifier) = Box(
    modifier = modifier.fillMaxWidth()
        .background(MaterialTheme.colors.background)
        .border(width = KEY_BORDER_WIDTH, color = KEY_BORDER_COLOR)
)
