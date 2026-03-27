package com.example.habitflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.compose.foundation.lazy.items


@Composable
fun ColorPicker(
    selectedColor: String,
    colors: List<String>,
    onColorSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .testTag("habit_color_picker"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(color.toColorInt()))
                    .selectedBorder(
                        color = color,
                        selectedColor = selectedColor
                    )
                    .clickable { onColorSelected(color) }
            ) { }
        }
    }
}

val habitColors = listOf(
    "#F44336",
    "#FF9800",
    "#FFEB3B",
    "#4CAF50",
    "#2196F3",
    "#9C27B0",
    "#E91E63",
    "#607D8B"
)

@Composable
private fun Modifier.selectedBorder(color: String, selectedColor: String): Modifier =
    this.then(
        if (color == selectedColor) Modifier.border(
            2.dp,
            MaterialTheme.colorScheme.onSurface,
            CircleShape
        )
        else Modifier
    )
