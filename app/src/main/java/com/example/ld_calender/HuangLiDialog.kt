package com.example.ld_calender

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 老黄历详情页（全屏展示）。
 *
 * 单击日历页下方的老黄历摘要框（广告位）后弹出。
 * 完整展示老黄历信息，字号比摘要框大，内容外部的方框与其它页面（年/月/周/日视图）的外部框相同样式。
 */
@Composable
fun HuangLiDialog(
    year: Int,
    month: Int,
    day: Int,
    onDismiss: () -> Unit
) {
    val deepGreen = appFontColor()
    val hl = remember(year, month, day) { LaoHuangLi.getHuangLi(year, month, day) }
    val weekday = LunarCalendar.getWeekdayChinese(year, month, day)
    val lunarString = LunarCalendar.getLunarString(year, month, day)
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBgColor())
    ) {
        // Title bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(appFontColor().copy(alpha = 0.15f))
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "老黄历",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = deepGreen
            )
        }

        // Content (scrollable): framed box with big-font Huang-Li details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Outer frame — same style as other pages (20dp radius + 2dp border)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(appBgColor(), shape)
                    .border(2.dp, deepGreen, shape)
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 日期标题：根据可用宽度自动调整字号，保证恰好一行不换行
                    DateTitle(year, month, day, weekday, deepGreen)

                    Spacer(modifier = Modifier.height(10.dp))

                    // 农历
                    Text(
                        text = lunarString,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = deepGreen,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = deepGreen.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    InfoRow("年干支", "${hl.yearGanzhi}年（${hl.zodiac}）", deepGreen)
                    InfoRow("月干支", "${hl.monthGanzhi}月", deepGreen)
                    InfoRow("日干支", "${hl.dayGanzhi}日", deepGreen)
                    InfoRow("值神", hl.twelveGod, deepGreen)
                    InfoRow("星宿", "${hl.star}宿（${hl.starRank}）", deepGreen)
                    InfoRow("冲煞", hl.chong, deepGreen)
                    InfoRow("煞方", "煞${hl.shaDirection}", deepGreen)

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = deepGreen.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 宜
                    MultiLineRow("宜", hl.yi.joinToString("、"), deepGreen)
                    Spacer(modifier = Modifier.height(8.dp))
                    // 忌
                    MultiLineRow("忌", hl.ji.joinToString("、"), deepGreen)
                }
            }
        }

        // Close button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(deepGreen)
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = 32.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "关闭",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * 单行信息：左侧标签 + 右侧值。
 */
@Composable
private fun InfoRow(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.width(104.dp)
        )
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            lineHeight = 32.sp
        )
    }
}

/**
 * 多行信息（宜/忌）：标签 + 可换行的值。
 */
@Composable
private fun MultiLineRow(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.width(104.dp)
        )
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = color,
            lineHeight = 34.sp
        )
    }
}

/**
 * 日期标题：根据可用宽度自动缩放字号（28sp → 18sp），
 * 保证"年月日 + 星期几"恰好显示在一行、不会换行。
 */
@Composable
private fun DateTitle(
    year: Int,
    month: Int,
    day: Int,
    weekday: String,
    color: Color
) {
    val text = "${year}年${month}月${day}日 周$weekday"
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val availableWidthPx = with(density) { maxWidth.roundToPx() }
        var bestSize = 18.sp
        for (sizeSp in 28 downTo 18) {
            val result = textMeasurer.measure(
                text = AnnotatedString(text),
                style = TextStyle(
                    fontSize = sizeSp.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            if (result.size.width <= availableWidthPx) {
                bestSize = sizeSp.sp
                break
            }
        }
        Text(
            text = text,
            style = TextStyle(
                fontSize = bestSize,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            ),
            maxLines = 1,
            overflow = TextOverflow.Clip,
            modifier = Modifier.fillMaxWidth()
        )
    }
}