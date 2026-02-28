package com.example.habitflow.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

@Composable
fun ColorPicker(
    selectedColor: String,
    colors: List<String>,
    onColorSelected: (String) -> Unit
) {
    FlowRow {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(color.toColorInt()))
                    .then(
                        if (color == selectedColor)
                            Modifier.border(2.dp, Color.Black, CircleShape)
                        else Modifier
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