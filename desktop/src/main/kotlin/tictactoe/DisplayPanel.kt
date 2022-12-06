package tictactoe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DisplayPanel(
    modifier: Modifier,
    game: Game
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .padding(CALCULATOR_PADDING)
            .background(Color.White)
            .border(color = Color.Gray, width = 1.dp)
            .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = game.text,
            style = TextStyle(
                fontSize = 48.sp,
            ),
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            maxLines = 1,
        )
    }
}
