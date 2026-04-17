package com.example.renovasriv4

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ColorScheme
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Typography
import androidx.tv.material3.darkColorScheme

val Primary = Color(0xFFC6C6C6)
val Surface = Color(0xFF131313)
val OnSurface = Color(0xFFE5E2E1)
val OnSurfaceVariant = Color(0xFFC5C6CA)
val Tertiary = Color(0xFFEEBD8E)
val SurfaceContainerHighest = Color(0xFF353534)
val SurfaceContainerLow = Color(0xFF1C1B1B)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    surface = Surface,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    tertiary = Tertiary,
    surfaceVariant = SurfaceContainerHighest,
    background = Surface
)

@Composable
fun HomeLivingTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        shapes = MaterialTheme.shapes.copy(
            small = RoundedCornerShape(2.dp),
            medium = RoundedCornerShape(4.dp),
            large = RoundedCornerShape(8.dp)
        ),
        content = content
    )
}
