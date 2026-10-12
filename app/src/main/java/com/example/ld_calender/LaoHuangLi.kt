package com.example.ld_calender

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * 老黄历（历书）数据计算。
 *
 * 所有规则均基于公开的干支纪法、建除十二神、冲煞、地支六冲、二十八星宿等传统历法公知内容：
 *  - 干支纪年：以立春为界
 *  - 干支纪月：月支固定（正月寅、二月卯...），月干按"五虎遁"推算
 *  - 干支纪日：以 1900-01-01 为甲戌日（标准历书锚点，连续纪日），适用于 1900~2099
 *  - 建除十二神：按正月建寅、每月从当月节气的固定次序排列
 *  - 冲煞：日支与其他地支六冲
 *  - 二十八星宿：以 1900-01-01 为角宿，每 28 日轮转
 *  - 宜忌：按公知的传统规则映射（建日、除日、满日、平日、定日、执日、破日、危日、成日、收日、开日、闭日）
 */
object LaoHuangLi {

    // ========== 干支基础 ==========

    private val gan = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
    private val zhi = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")
    private val wuHuDun = arrayOf(
        // 年干 -> 正月（寅月）天干起始索引
        "丙", // 甲、己年
        "戊", // 乙、庚年
        "庚", // 丙、辛年
        "壬", // 丁、壬年
        "甲"  // 戊、癸年
    )

    // 月支（农历正月 = 寅月，依次类推）
    private val monthBranch = arrayOf(
        2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 1
    )

    // ========== 建除十二神 ==========

    private val twelveGods = arrayOf(
        "建", "除", "满", "平", "定", "执",
        "破", "危", "成", "收", "开", "闭"
    )

    // 每个月的"建日"地支：正月建寅（月支为寅时建日 = 寅），依次为 寅卯辰巳午未申酉戌亥子丑
    private val monthBuildBranch = intArrayOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, 1)

    // ========== 冲煞 ==========

    // 地支六冲：子午冲、丑未冲、寅申冲、卯酉冲、辰戌冲、巳亥冲
    private val chongPair = intArrayOf(6, 7, 8, 9, 10, 11, 0, 1, 2, 3, 4, 5)

    // 生肖
    private val zodiac = arrayOf("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪")

    // 方位
    private val chongDirection = arrayOf("正南", "西南", "正南", "正东", "东南", "正北", "正北", "东南", "正西", "正西", "西北", "西北")

    // ========== 二十八星宿 ==========

    private val stars28 = arrayOf(
        "角", "亢", "氐", "房", "心", "尾", "箕",
        "斗", "牛", "女", "虚", "危", "室", "壁",
        "奎", "娄", "胃", "昴", "毕", "觜", "参",
        "井", "鬼", "柳", "星", "张", "翼", "轸"
    )

    // 星宿对应的传统吉凶
    private val starRanks = arrayOf(
        "吉", "凶", "吉", "吉", "凶", "吉", "吉", // 角亢氐房心尾箕
        "吉", "凶", "吉", "凶", "凶", "吉", "吉", // 斗牛女虚危室壁
        "吉", "吉", "吉", "凶", "吉", "凶", "吉", // 奎娄胃昴毕觜参
        "吉", "凶", "吉", "吉", "凶", "吉", "凶"  // 井鬼柳星张翼轸
    )

    // ========== 宜忌 ==========

    private val yiMap = mapOf(
        "建" to listOf("出行", "上任", "会友", "上书"),
        "除" to listOf("除服", "疗病", "出行", "拆卸"),
        "满" to listOf("祈福", "祭祀", "开市", "修造"),
        "平" to listOf("修屋", "涂泥", "饰垣"),
        "定" to listOf("祭祀", "祈福", "嫁娶", "建造", "开市"),
        "执" to listOf("捕捉", "断蚁", "畋猎"),
        "破" to listOf("破屋", "坏垣", "求医"),
        "危" to listOf("祭祀", "祈福", "安床"),
        "成" to listOf("入学", "开市", "嫁娶", "求医", "交易"),
        "收" to listOf("纳财", "入宅", "嫁娶", "收购"),
        "开" to listOf("祭祀", "祈福", "求嗣", "开业", "出行"),
        "闭" to listOf("筑堤", "修墙", "安葬")
    )

    private val jiMap = mapOf(
        "建" to listOf("开市", "入宅", "安葬"),
        "除" to listOf("求医", "嫁娶", "开市"),
        "满" to listOf("安葬", "余事勿取"),
        "平" to listOf("开市", "嫁娶", "安葬"),
        "定" to listOf("移徙", "出行", "开市"),
        "执" to listOf("开市", "安葬", "嫁娶"),
        "破" to listOf("嫁娶", "出行", "开市", "安葬"),
        "危" to listOf("出行", "安葬", "开市"),
        "成" to listOf("安葬", "诉讼"),
        "收" to listOf("安葬", "开市"),
        "开" to listOf("安葬", "开仓"),
        "闭" to listOf("开市", "出行", "嫁娶")
    )

    // ========== 日期锚点 ==========

    // 1900-01-01 的干支：甲戌日
    private const val BASE_JDN = 2415021
    private const val BASE_GAN = 0
    private const val BASE_ZHI = 10
    private const val BASE_STAR = 0 // 1900-01-01 为角宿

    // ========== 数据类 ==========

    data class HuangLi(
        val yearGanzhi: String,        // 年干支（以立春为界）
        val monthGanzhi: String,       // 月干支
        val dayGanzhi: String,         // 日干支
        val zodiac: String,            // 生肖
        val twelveGod: String,         // 建除十二神
        val chong: String,             // 冲煞，如 "冲龙(甲辰)"
        val shaDirection: String,      // 煞方
        val star: String,              // 二十八星宿
        val starRank: String,          // 星宿吉凶：吉/凶
        val yi: List<String>,          // 宜
        val ji: List<String>,          // 忌
    )

    // ========== 主入口 ==========

    /**
     * 计算指定公历日期的老黄历信息。
     */
    fun getHuangLi(year: Int, month: Int, day: Int): HuangLi {
        val cal = GregorianCalendar(year, month - 1, day)
        val jd = jdn(year, month, day)

        // 日干支（连续纪日，标准历书锚点）
        val offset = jd - BASE_JDN
        val dayGanIdx = (BASE_GAN + offset) mod 10
        val dayZhiIdx = (BASE_ZHI + offset) mod 12
        val dayGanzhi = gan[dayGanIdx] + zhi[dayZhiIdx]

        // 年干支（立春为界）
        val liChun = getLiChun(year)
        val actualYear = if (cal.before(liChun)) year - 1 else year
        val yearGanIdx = (actualYear - 4) mod 10
        val yearZhiIdx = (actualYear - 4) mod 12
        val yearGanzhi = gan[yearGanIdx] + zhi[yearZhiIdx]

        // 月干支：以节气为界（立春起为正月寅月）
        val monthInfo = getMonthGanzhi(year, month, day)
        val monthGanzhi = gan[monthInfo.gan] + zhi[monthInfo.zhi]
        val monthIndex = monthInfo.monthIndex // 0=寅月, 1=卯月...

        // 生肖
        val animalIdx = yearZhiIdx
        val animal = zodiac[animalIdx]

        // 建除十二神
        val buildBranch = monthBuildBranch[monthIndex]
        val godIndex = ((dayZhiIdx - buildBranch) mod 12 + 12) mod 12
        val twelveGod = twelveGods[godIndex]

        // 冲煞：日支的六冲
        val chongZhiIdx = chongPair[dayZhiIdx]
        val chongGan = getChongGan(chongZhiIdx)
        val chongZhi = zhi[chongZhiIdx]
        val chongAnimal = zodiac[chongZhiIdx]
        val chongText = "冲${chongAnimal}($chongGan$chongZhi)"

        // 煞方
        val sha = chongDirection[chongZhiIdx]

        // 二十八星宿
        val starIdx = ((BASE_STAR + offset) mod 28 + 28) mod 28
        val star = stars28[starIdx]
        val starRank = starRanks[starIdx]

        // 宜忌
        val yi = yiMap[twelveGod] ?: emptyList()
        val ji = jiMap[twelveGod] ?: emptyList()

        return HuangLi(
            yearGanzhi = yearGanzhi,
            monthGanzhi = monthGanzhi,
            dayGanzhi = dayGanzhi,
            zodiac = animal,
            twelveGod = twelveGod,
            chong = chongText,
            shaDirection = sha,
            star = star,
            starRank = starRank,
            yi = yi,
            ji = ji
        )
    }

    // ========== 工具方法 ==========

    private infix fun Int.mod(n: Int): Int = ((this % n) + n) % n

    private fun jdn(year: Int, month: Int, day: Int): Int {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
    }

    /**
     * 计算指定年份立春（约2月4日）的 Calendar。
     */
    private fun getLiChun(year: Int): Calendar {
        return GregorianCalendar(year, Calendar.FEBRUARY, 4)
    }

    /**
     * 计算指定公历日期的月干支。
     * 以节气为界：立春（约2月4日）起为寅月，惊蛰（约3月6日）起为卯月，依次类推。
     */
    private fun getMonthGanzhi(year: Int, month: Int, day: Int): MonthGanzhi {
        val cal = GregorianCalendar(year, month - 1, day)

        // 各月"节"的近似日期（从立春开始）
        val jieQiDays = intArrayOf(4, 6, 6, 6, 6, 7, 8, 8, 8, 8, 7, 7)
        val jieQiMonths = intArrayOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 1)

        // 判断当前日期处于哪个节之后（从最近的节往前找）
        var monthIndex = 0 // 默认寅月
        for (i in 11 downTo 0) {
            val jMonth = jieQiMonths[i]
            val jDay = jieQiDays[i]
            val jYear = if (jMonth > month) year - 1 else year
            val jDate = GregorianCalendar(jYear, jMonth - 1, jDay)
            if (!cal.before(jDate)) {
                monthIndex = i
                break
            }
        }

        // 月支
        val zhiIdx = monthBranch[monthIndex]

        // 月干：五虎遁
        val liChun = getLiChun(year)
        val actualYear = if (cal.before(liChun)) year - 1 else year
        val yearGanIdx = (actualYear - 4) mod 10
        val startGan = wuHuDun[(yearGanIdx / 2) % 5]
        val startGanIdx = gan.indexOf(startGan)
        val ganIdx = (startGanIdx + monthIndex) mod 10

        return MonthGanzhi(ganIdx, zhiIdx, monthIndex)
    }

    private data class MonthGanzhi(val gan: Int, val zhi: Int, val monthIndex: Int)

    /**
     * 计算冲煞所配的天干（传统历书约定的固定配干）。
     */
    private fun getChongGan(chongZhi: Int): String {
        return when (chongZhi) {
            0 -> "癸"  // 子
            1 -> "己"  // 丑
            2 -> "甲"  // 寅
            3 -> "乙"  // 卯
            4 -> "戊"  // 辰
            5 -> "丁"  // 巳
            6 -> "丙"  // 午
            7 -> "己"  // 未
            8 -> "庚"  // 申
            9 -> "辛"  // 酉
            10 -> "戊" // 戌
            11 -> "丁" // 亥
            else -> ""
        }
    }
}