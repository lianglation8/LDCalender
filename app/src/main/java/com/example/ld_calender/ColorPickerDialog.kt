package com.example.ld_calender

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * A full-screen page for picking a color.
 * Fully covers the previous screen (e.g. the settings page) so no
 * overlapping/ghosting of the UI below can occur.
 */
@Composable
fun ColorPickerPage(
    title: String,
    initialColor: Color,
    defaultColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val fontColor = appFontColor()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBgColor())
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Title bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = fontColor
            )
        }

        ColorPickerContent(
            initialColor = initialColor,
            defaultColor = defaultColor,
            onColorSelected = onColorSelected,
            onDismiss = onDismiss
        )
    }
}

/**
 * A dialog for picking a color.
 * Shows a palette of preset colors plus RGB sliders for full customization.
 */
@Composable
fun ColorPickerDialog(
    title: String,
    initialColor: Color,
    defaultColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val fontColor = appFontColor()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(appBgColor())
            .clickable(enabled = false) {} // Prevent click-through
            .padding(24.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        ColorPickerContent(
            initialColor = initialColor,
            defaultColor = defaultColor,
            onColorSelected = onColorSelected,
            onDismiss = onDismiss
        )
    }
}

/** Shared picker body used by [ColorPickerPage] and [ColorPickerDialog]. */
@Composable
private fun ColorPickerContent(
    initialColor: Color,
    defaultColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var red by remember { mutableStateOf((initialColor.red * 255).roundToInt().coerceIn(0, 255)) }
    var green by remember { mutableStateOf((initialColor.green * 255).roundToInt().coerceIn(0, 255)) }
    var blue by remember { mutableStateOf((initialColor.blue * 255).roundToInt().coerceIn(0, 255)) }

    val currentColor = remember(red, green, blue) { Color(red / 255f, green / 255f, blue / 255f) }

    val fontColor = appFontColor()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Preview swatch
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(currentColor)
                .border(2.dp, Color(0xFFCCCCCC), RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Preset palette
        Text(
            text = "预设颜色",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = fontColor
        )
        Spacer(modifier = Modifier.height(10.dp))

        val presetColors = listOf(
            Color(0xFF2E7D32), Color(0xFF1B5E20), Color(0xFF388E3C), Color(0xFF66BB6A),
            Color(0xFFE65100), Color(0xFFFF8F00), Color(0xFFF9A825), Color(0xFFFFD600),
            Color(0xFFC62828), Color(0xFFE53935), Color(0xFFAD1457), Color(0xFFD81B60),
            Color(0xFF1E88E5), Color(0xFF039BE5), Color(0xFF00838F), Color(0xFF00ACC1),
            Color(0xFF6A1B9A), Color(0xFF8E24AA), Color(0xFF5D4037), Color(0xFF795548),
            Color(0xFF37474F), Color(0xFF546E7A), Color(0xFF000000), Color(0xFF666666)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            presetColors.chunked(6).forEach { rowColors ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = 2.dp,
                                    color = if (color == currentColor) fontColor else Color(0xFFDDDDDD),
                                    shape = CircleShape
                                )
                                .clickable {
                                    red = (color.red * 255).roundToInt()
                                    green = (color.green * 255).roundToInt()
                                    blue = (color.blue * 255).roundToInt()
                                }
                        )
                    }
                    repeat(6 - rowColors.size) {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // RGB sliders
        SliderRow(label = "红", value = red, onValueChange = { red = it })
        SliderRow(label = "绿", value = green, onValueChange = { green = it })
        SliderRow(label = "蓝", value = blue, onValueChange = { blue = it })

        Spacer(modifier = Modifier.height(20.dp))

        // Buttons: Cancel | Restore default | Confirm
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE0E0E0))
                    .clickable(onClick = onDismiss)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "取消", fontSize = 14.sp, color = fontColor)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(fontColor.copy(alpha = 0.15f))
                    .clickable { onColorSelected(defaultColor) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "恢复默认", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = fontColor)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(fontColor)
                    .clickable { onColorSelected(currentColor) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "确定", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun SliderRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    val fontColor = appFontColor()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = fontColor,
            modifier = Modifier.width(30.dp)
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt().coerceIn(0, 255)) },
            valueRange = 0f..255f,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value.toString(),
            fontSize = 14.sp,
            color = fontColor,
            textAlign = TextAlign.End,
            modifier = Modifier.width(32.dp)
        )
    }
}