package com.example.ld_calender

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 技术说明页面（全屏展示）。
 * 从设置页面的"技术说明"入口进入，
 * 介绍应用的使用技巧（快捷跳转、年份选择、付费用户黄历详情等）。
 */
@Composable
fun TechGuideDialog(
    onDismiss: () -> Unit
) {
    val deepGreen = appFontColor()
    val lightGreenBg = deepGreen.copy(alpha = 0.15f)

    // Full-screen overlay
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appBgColor())
    ) {
        // Title bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(lightGreenBg)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "技术说明",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = deepGreen
            )
        }

        // Content (scrollable when taller than the screen on small devices)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "以下是本软件的常用操作技巧，帮助您更快上手：",
                fontSize = 15.sp,
                color = deepGreen.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Item 1
            GuideItem(number = "1", text = "长按年、月、周、日按钮，可以立刻定位到当前对应的日历。")

            Spacer(modifier = Modifier.height(16.dp))

            // Item 2
            GuideItem(
                number = "2",
                text = "在“年”页面中，点击上方的年份，会弹出1900~2100一共200年的选择图标，点击这些图标，可以跳转到相应年份。"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Item 3
            GuideItem(
                number = "3",
                text = "如果您是付费用户，单击网页下面的黄历框内容，可以弹出更加详细的黄历信息。"
            )
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
 * 单条技术说明：左侧序号圆标 + 右侧说明文字。
 */
@Composable
private fun GuideItem(
    number: String,
    text: String
) {
    val deepGreen = appFontColor()
    val itemBg = deepGreen.copy(alpha = 0.06f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(itemBg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Number badge
        Box(
            modifier = Modifier
                .width(28.dp)
                .clip(RoundedCornerShape(50))
                .background(deepGreen)
                .padding(vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Description text
        Text(
            text = text,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            color = deepGreen,
            modifier = Modifier.weight(1f)
        )
    }
}
