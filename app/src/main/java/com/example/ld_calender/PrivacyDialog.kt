package com.example.ld_calender

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 用户协议全文 */
private val USER_AGREEMENT_TEXT = """
欢迎使用欧登日历（以下简称"本软件"）。在使用本软件之前，请您仔细阅读并充分理解本《用户协议》（以下简称"本协议"）。您下载、安装、使用本软件，即视为您已阅读并同意接受本协议的全部条款。

一、服务说明
1. 本软件是一款为您提供公历、农历、节气、法定节假日、纪念日提醒及日程计划等功能的日历工具。
2. 本软件向您提供"免费 + 广告"和"付费去广告"两种使用方式：
   （1）免费模式：您可以免费使用本软件的全部功能，但需要接受我们展示的广告。
   （2）付费模式：您可以通过付费订阅或买断的方式去除广告，付费后我们将不再向您展示广告。
3. 付费去广告属于虚拟服务，一经购买成功，不支持退款（法律法规另有规定的除外）。付费服务的具体价格、时长以购买页面实际展示为准。

二、使用规则
1. 您应遵守国家法律法规，不得利用本软件从事任何违法、违规或侵犯他人合法权益的活动。
2. 您不得对本软件进行反向工程、反编译、破解、篡改，或尝试绕过本软件的任何付费与广告机制。
3. 您不得使用任何自动化工具批量抓取、复制、传播本软件的数据或内容。

三、广告说明
1. 在免费模式下，本软件可能会展示第三方广告，包括但不限于横幅广告、开屏广告等。
2. 广告内容由第三方广告平台提供，本软件不对广告内容的真实性、准确性或完整性作出任何承诺。您与广告主之间因广告产生的任何交易或纠纷，由您与广告主自行解决，与本软件无关。
3. 您理解并同意，展示广告是我们为持续提供免费服务所采取的方式。

四、付费去广告
1. 您购买"去广告"服务后，本软件将不再向您展示我们自有及合作平台推送的广告。
2. 去广告服务仅针对当前版本的软件。如因系统升级、商店政策等原因需要重新购买，我们会提前在软件内说明。
3. 去广告权益与您的账号（如适用）绑定，请在卸载或更换设备前确认您的账号信息。

五、免责声明
1. 本软件提供的日历、节气、节假日等数据仅供参考，请以官方发布的信息为准。因数据来源可能存在延迟或误差，我们不承担因数据不准确而造成的任何损失。
2. 因不可抗力、网络故障、系统维护等原因导致本软件暂时无法使用，我们不承担相应责任。
3. 在法律允许的最大范围内，本软件不对因使用或无法使用本软件而造成的任何间接、偶然或必然损失承担责任。

六、协议的修改
1. 我们有权根据法律法规变化或业务需要，适时修改本协议。修改后的协议将在本软件内公布。
2. 若您不同意修改后的协议，您可以停止使用本软件；如您继续使用，即视为您接受修改后的协议。

七、其他
1. 本协议的订立、执行与解释均适用中华人民共和国法律。
2. 若本协议的任何条款被认定为无效或不可执行，不影响其余条款的效力。
3. 如您对本协议有任何疑问，可通过软件"关于"页面中的"发送电子邮件"与我们联系。

感谢您选择欧登日历，祝您使用愉快！
""".trimIndent()

/** 隐私政策全文 */
private val PRIVACY_POLICY_TEXT = """
欢迎使用欧登日历（以下简称"本软件"）。我们深知个人信息对您的重要性，并承诺以严谨、审慎的态度保护您的隐私。本《隐私政策》将向您说明我们如何处理信息。您使用本软件，即视为您已阅读并同意本政策。

一、我们如何收集和使用信息
1. 我们坚持"不收集用户隐私"的原则。
2. 本软件的核心功能（日历展示、农历查询、节假日、节日、日程计划等）均在您本地的设备上运行。我们不会收集、上传或存储您的姓名、手机号、通讯录、位置、相册、日历内容、日程计划等任何个人信息。
3. 您的日程计划、自定义节日等数据仅保存在您的本地设备中，我们无法访问这些数据。请您妥善保管您的设备，并在卸载软件前自行备份需要的数据。

二、我们如何使用网络权限
1. 为了向您提供最新的国家法定节假日安排及软件更新信息，本软件会在联网状态下从服务器获取相关的公开数据。
2. 我们不会通过网络收集您的个人身份信息；联网获取的内容均为公开的节假日数据或软件版本信息。

三、广告服务说明
1. 在免费模式下，本软件会展示广告。广告服务可能由第三方广告平台（如Google AdMob等）提供。
2. 第三方广告平台可能会根据其自身的隐私政策，通过设备标识符等方式提供更相关的广告内容。这些行为受第三方平台的隐私政策约束，建议您查阅相关平台的隐私政策。
3. 我们不会将您的任何个人信息提供给广告平台用于定向广告；您也可以选择付费去广告，以完全避免广告展示。

四、权限使用说明
本软件不会申请与个人隐私相关的敏感权限（如通讯录、定位、相册、麦克风、摄像头等）。本软件仅使用网络权限（INTERNET）用于获取公开的节假日数据及软件更新信息。

五、信息存储
1. 本软件产生的所有用户数据（如日程计划、自定义节日、界面设置等）均存储在您设备的本地存储中。
2. 卸载本软件后，相应的本地数据将被删除，我们无法为您恢复，敬请留意备份。

六、未成年人保护
本软件不收集任何个人信息，不涉及针对未成年人的定向内容推送。若您是未成年人，请在监护人指导下合理使用本软件。

七、政策的更新
我们可能会适时更新本政策，更新后的政策将在本软件内公布。重大变更时，我们会通过软件内显著位置提示您。您继续使用本软件，即视为您接受更新后的政策。

八、联系我们
如您对本隐私政策有任何疑问、意见或建议，可通过软件"关于"页面中的"发送电子邮件"与我们联系。我们将在收到您的反馈后尽快予以答复。

感谢您的信任与支持！
""".trimIndent()

/**
 * 隐私与权限页面（全屏展示）。
 * 包含"用户协议"和"隐私政策"两行，点击后可展开查看完整内容。
 */
@Composable
fun PrivacyDialog(
    onDismiss: () -> Unit
) {
    val deepGreen = appFontColor()
    var showAgreement by remember { mutableStateOf(false) }
    var showPolicy by remember { mutableStateOf(false) }

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
                text = "隐私与权限",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = deepGreen
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            // Row 1: 用户协议
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(appFontColor().copy(alpha = 0.06f))
                    .clickable { showAgreement = !showAgreement }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "用户协议",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = deepGreen
                )
                Text(
                    text = if (showAgreement) "－" else "＋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen
                )
            }

            // Expandable agreement content
            AnimatedVisibility(visible = showAgreement) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .heightIn(max = 400.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(appFontColor().copy(alpha = 0.04f))
                        .border(1.dp, appFontColor().copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = USER_AGREEMENT_TEXT,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = deepGreen,
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Row 2: 隐私政策
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(appFontColor().copy(alpha = 0.06f))
                    .clickable { showPolicy = !showPolicy }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "隐私政策",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = deepGreen
                )
                Text(
                    text = if (showPolicy) "－" else "＋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = deepGreen
                )
            }

            // Expandable policy content
            AnimatedVisibility(visible = showPolicy) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .heightIn(max = 400.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(appFontColor().copy(alpha = 0.04f))
                        .border(1.dp, appFontColor().copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = PRIVACY_POLICY_TEXT,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = deepGreen,
                        textAlign = TextAlign.Start
                    )
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