package com.example.ld_calender

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 邮件反馈页面（全屏展示）。
 * 用户填写收件人、主题、内容后，点击"发送"调用系统邮件应用完成发送。
 */
@Composable
fun EmailFeedbackDialog(
    onDismiss: () -> Unit
) {
    val deepGreen = appFontColor()
    val context = LocalContext.current

    var recipient by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }

    fun sendEmail() {
        if (recipient.isBlank()) {
            Toast.makeText(context, "请填写收件人邮箱", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${recipient.trim()}")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient.trim()))
            putExtra(Intent.EXTRA_SUBJECT, subject.trim())
            putExtra(Intent.EXTRA_TEXT, content.trim())
        }
        try {
            context.startActivity(Intent.createChooser(intent, "选择邮件应用"))
            // 调起成功：切换为"已发送"状态
            emailSent = true
        } catch (e: Exception) {
            Toast.makeText(context, "未找到可用的邮件应用，发送失败", Toast.LENGTH_SHORT).show()
        }
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
                text = "邮件反馈",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = deepGreen
            )
        }

        // Form
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // ===== 已发送状态提示条 =====
            if (emailSent) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(deepGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "邮件已发送！\n请在邮件应用中确认发送结果。",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = deepGreen
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ===== 收件人 =====
            Text(
                text = "收件人",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = deepGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = recipient,
                onValueChange = { recipient = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "请输入收件人邮箱",
                        color = deepGreen.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                },
                singleLine = true,
                enabled = !emailSent,
                textStyle = TextStyle(color = deepGreen, fontSize = 16.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = deepGreen,
                    unfocusedBorderColor = deepGreen.copy(alpha = 0.4f),
                    disabledBorderColor = deepGreen.copy(alpha = 0.25f),
                    disabledTextColor = deepGreen.copy(alpha = 0.6f),
                    cursorColor = deepGreen
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== 主题 =====
            Text(
                text = "主题",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = deepGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "请输入邮件主题",
                        color = deepGreen.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                },
                singleLine = true,
                enabled = !emailSent,
                textStyle = TextStyle(color = deepGreen, fontSize = 16.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = deepGreen,
                    unfocusedBorderColor = deepGreen.copy(alpha = 0.4f),
                    disabledBorderColor = deepGreen.copy(alpha = 0.25f),
                    disabledTextColor = deepGreen.copy(alpha = 0.6f),
                    cursorColor = deepGreen
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ===== 内容 =====
            Text(
                text = "内容",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = deepGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                placeholder = {
                    Text(
                        text = "请输入反馈内容",
                        color = deepGreen.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                },
                enabled = !emailSent,
                textStyle = TextStyle(color = deepGreen, fontSize = 16.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = deepGreen,
                    unfocusedBorderColor = deepGreen.copy(alpha = 0.4f),
                    disabledBorderColor = deepGreen.copy(alpha = 0.25f),
                    disabledTextColor = deepGreen.copy(alpha = 0.6f),
                    cursorColor = deepGreen
                )
            )
        }

        // ===== Bottom buttons =====
        if (emailSent) {
            // 已发送状态：仅显示"完成"按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(deepGreen)
                        .clickable(onClick = onDismiss)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "完成",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        } else {
            // 编辑状态：显示"返回"和"发送"按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(deepGreen.copy(alpha = 0.2f))
                    .clickable(onClick = onDismiss)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "返回",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(deepGreen)
                    .clickable(onClick = ::sendEmail)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "发送",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        }
    }
}
