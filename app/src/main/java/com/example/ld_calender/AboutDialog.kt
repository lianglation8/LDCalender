package com.example.ld_calender

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 关于页面（全屏展示）。
 * 介绍软件 Logo、名称、软件介绍、版本、作者与反馈入口。
 */
@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    val deepGreen = appFontColor()
    val context = LocalContext.current
    var showEmailFeedback by remember { mutableStateOf(false) }

    // 邮件反馈页面：全屏覆盖，隐藏关于页面内容，避免背景重叠
    if (showEmailFeedback) {
        EmailFeedbackDialog(
            onDismiss = {
                showEmailFeedback = false
            }
        )
        return
    }

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
                text = "关于",
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
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Logo + app name (centered)
            Spacer(modifier = Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "欧登日历 Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "欧登日历",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = deepGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 2. App description
            Text(
                text = "一个专为中老年用户提供软件服务的品牌。简洁、方便是我们尽力追求的目标。",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = deepGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Version
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "版本",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1.0",
                    fontSize = 16.sp,
                    color = deepGreen
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "检查更新",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = deepGreen,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            // 后续将提供链接进行更新
                            Toast.makeText(
                                context,
                                "已是最新版本",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Author
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "作者",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "由LationLiang开发",
                    fontSize = 16.sp,
                    color = deepGreen
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 5. Feedback
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "反馈",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "发送电子邮件",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = deepGreen,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            showEmailFeedback = true
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
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
