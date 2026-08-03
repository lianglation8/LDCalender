package com.example.ld_calender

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Utility class to convert Gregorian calendar dates to Chinese lunar calendar dates.
 */
object LunarCalendar {

    private val lunarInfo = longArrayOf(
        0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
        0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
        0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
        0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
        0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
        0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5b0, 0x14573, 0x052b0, 0x0a9a8, 0x0e950, 0x06aa0,
        0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
        0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b6a0, 0x195a6,
        0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
        0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x05ac0, 0x0ab60, 0x096d5, 0x092e0,
        0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
        0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
        0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
        0x05aa0, 0x076a3, 0x096d0, 0x04afb, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
        0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0,
        0x14b63, 0x09370, 0x049f8, 0x04970, 0x064b0, 0x168a6, 0x0ea50, 0x06aa0, 0x1a6c4, 0x0aae0,
        0x092e0, 0x0d2e3, 0x0c960, 0x0d557, 0x0d4a0, 0x0da50, 0x05d55, 0x056a0, 0x0a6d0, 0x055d4,
        0x052d0, 0x0a9b8, 0x0a950, 0x0b4a0, 0x0b6a6, 0x0ad50, 0x055a0, 0x0aba4, 0x0a5b0, 0x052b0,
        0x0b273, 0x06930, 0x07337, 0x06aa0, 0x0ad50, 0x14b55, 0x04b60, 0x0a570, 0x054e4, 0x0d160,
        0x0e968, 0x0d520, 0x0daa0, 0x16aa6, 0x056d0, 0x04ae0, 0x0a9d4, 0x0a4d0, 0x0d150, 0x0f252,
        0x0d520
    )

    private val gan = arrayOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")
    private val zhi = arrayOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")

    data class LunarDate(
        val year: Int,
        val month: Int,
        val day: Int,
        val isLeap: Boolean
    )

    /**
     * Returns the Chinese lunar date string for the given Gregorian date.
     * e.g. "农历乙巳年六月初六"
     */
    fun getLunarString(year: Int, month: Int, day: Int): String {
        val cal = GregorianCalendar(year, month - 1, day)
        val lunar = solarToLunar(cal)
        val yearName = getYearName(lunar.year)
        val monthName = if (lunar.isLeap) "闰${getMonthName(lunar.month)}" else getMonthName(lunar.month)
        val dayName = getDayName(lunar.day)
        return "农历${yearName}年${monthName}$dayName"
    }

    /**
     * Returns the Gregorian date formatted in Chinese style.
     */
    fun getDateString(year: Int, month: Int, day: Int): String {
        val weekDays = arrayOf("日", "一", "二", "三", "四", "五", "六")
        val cal = GregorianCalendar(year, month - 1, day)
        val weekday = weekDays[cal.get(Calendar.DAY_OF_WEEK) - 1]
        return "${year}年${month}月${day}日 周$weekday"
    }

    /**
     * Returns the Chinese weekday character for the given Gregorian date.
     * e.g. "一" for Monday, "日" for Sunday
     */
    fun getWeekdayChinese(year: Int, month: Int, day: Int): String {
        val weekDays = arrayOf("日", "一", "二", "三", "四", "五", "六")
        val cal = GregorianCalendar(year, month - 1, day)
        return weekDays[cal.get(Calendar.DAY_OF_WEEK) - 1]
    }

    /**
     * Returns just the lunar day name for the given Gregorian date.
     * e.g. "初一", "十五", "廿三"
     */
    fun getLunarDayName(year: Int, month: Int, day: Int): String {
        val cal = GregorianCalendar(year, month - 1, day)
        val lunar = solarToLunar(cal)
        return getDayName(lunar.day)
    }

    // Compute Julian Day Number (integer arithmetic, no timezone dependency)
    private fun jdn(year: Int, month: Int, day: Int): Int {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
    }

    private fun solarToLunar(solar: GregorianCalendar): LunarDate {
        // Base date: January 31, 1900 = Chinese New Year of 庚子年 (1900)
        // Use pure integer Julian Day Number to avoid any timezone artifacts
        val baseJDN = jdn(1900, 1, 31)
        val targetJDN = jdn(solar.get(Calendar.YEAR), solar.get(Calendar.MONTH) + 1, solar.get(Calendar.DAY_OF_MONTH))
        var dayOffset = (targetJDN - baseJDN).toLong()

        var lunarYear = 0
        var lunarMonth = 0
        var lunarDay = 0
        var isLeap = false

        var i = 1900
        while (i < 2100) {
            val daysInYear = daysInLunarYear(i)
            if (dayOffset < daysInYear) {
                lunarYear = i
                break
            }
            dayOffset -= daysInYear
            i++
        }

        val leapMonth = leapMonth(lunarYear)
        var leapHandled = false

        i = 1
        while (i <= 12 || (leapMonth > 0 && !leapHandled)) {
            if (leapMonth > 0 && i == leapMonth + 1 && !leapHandled) {
                i--
                leapHandled = true
                val leapDays = if (leapMonthDays(lunarYear) > 0) leapMonthDays(lunarYear) else 30
                if (dayOffset < leapDays) {
                    lunarMonth = i
                    lunarDay = dayOffset.toInt() + 1
                    isLeap = true
                    break
                }
                dayOffset -= leapDays
            } else {
                val monthDays = daysInLunarMonth(lunarYear, i)
                if (dayOffset < monthDays) {
                    lunarMonth = i
                    lunarDay = dayOffset.toInt() + 1
                    break
                }
                dayOffset -= monthDays
            }
            i++
        }

        return LunarDate(lunarYear, lunarMonth, lunarDay, isLeap)
    }

    private fun daysInLunarYear(year: Int): Long {
        var sum = 348L
        // MSB-first: Month 1 = bit 15 (0x8000), Month 12 = bit 4 (0x10)
        for (i in 1..12) {
            sum += if ((lunarInfo[year - 1900] and (0x8000L shr (i - 1))) != 0L) 1L else 0L
        }
        return sum + leapMonthDays(year)
    }

    private fun leapMonth(year: Int): Int {
        return (lunarInfo[year - 1900] and 0xf).toInt()
    }

    private fun leapMonthDays(year: Int): Long {
        return if (leapMonth(year) != 0) {
            if ((lunarInfo[year - 1900] and 0x10000) != 0L) 30 else 29
        } else 0
    }

    private fun daysInLunarMonth(year: Int, month: Int): Long {
        // MSB-first: Month 1 = bit 15 (0x8000), Month 12 = bit 4 (0x10)
        return if ((lunarInfo[year - 1900] and (0x8000L shr (month - 1))) != 0L) 30 else 29
    }

    private fun getYearName(year: Int): String {
        val g = gan[(year - 4) % 10]
        val z = zhi[(year - 4) % 12]
        return "$g$z"
    }

    private fun getMonthName(month: Int): String {
        val names = arrayOf(
            "正月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "冬月", "腊月"
        )
        return if (month in 1..12) names[month - 1] else ""
    }

    private fun getDayName(day: Int): String {
        val names = arrayOf(
            "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
            "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
            "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
        )
        return if (day in 1..30) names[day - 1] else ""
    }

    // ========== Holidays & Solar Terms ==========

    enum class SpecialDayType { FESTIVAL, SOLAR_TERM }

    data class SpecialDay(val name: String, val type: SpecialDayType)

    /**
     * Mutable list of user-defined custom lunar festivals.
     * Set this at app startup from CustomFestivalStorage.
     */
    var customFestivals: List<CustomFestivalStorage.CustomFestival> = emptyList()

    /**
     * Returns list of special days (festivals and solar terms) for the given Gregorian date.
     */
    fun getSpecialDays(solarYear: Int, solarMonth: Int, solarDay: Int): List<SpecialDay> {
        val result = mutableListOf<SpecialDay>()

        // Solar terms
        val term = getSolarTerm(solarYear, solarMonth, solarDay)
        if (term != null) {
            result.add(SpecialDay(term, SpecialDayType.SOLAR_TERM))
        }

        // Lunar festivals (based on lunar date)
        val cal = GregorianCalendar(solarYear, solarMonth - 1, solarDay)
        val lunar = solarToLunar(cal)

        // Custom festivals (user-defined)
        for (cf in customFestivals) {
            if (cf.lunarMonth == lunar.month && cf.lunarDay == lunar.day) {
                // For leap months: only show if includeLeap is true
                if (!lunar.isLeap || cf.includeLeap) {
                    result.add(SpecialDay(cf.name, SpecialDayType.FESTIVAL))
                }
            }
        }

        val lunarFestival = getLunarFestival(lunar.month, lunar.day, lunar.isLeap)
        if (lunarFestival != null) {
            result.add(SpecialDay(lunarFestival, SpecialDayType.FESTIVAL))
        }

        // 除夕: last day of 12th lunar month (day 29 or 30, whichever is the last)
        if (lunar.month == 12 && !lunar.isLeap) {
            val lastDayOf12thMonth = daysInLunarMonth(lunar.year, 12)
            if (lunar.day.toLong() == lastDayOf12thMonth) {
                // Only add if not already set as 春节 (which is 正月初一, not applicable here)
                if (lunarFestival == null) {
                    result.add(SpecialDay("除夕", SpecialDayType.FESTIVAL))
                }
            }
        }

        // Gregorian (solar) festivals
        val solarFestival = getSolarFestival(solarMonth, solarDay)
        if (solarFestival != null) {
            result.add(SpecialDay(solarFestival, SpecialDayType.FESTIVAL))
        }

        // Mother's Day, Father's Day
        val otherFestival = getVariableFestival(solarYear, solarMonth, solarDay)
        if (otherFestival != null) {
            result.add(SpecialDay(otherFestival, SpecialDayType.FESTIVAL))
        }

        return result
    }

    /**
     * Returns a combined special-day text for display, e.g. "春节" or "清明" or "" if none.
     */
    fun getSpecialDayText(solarYear: Int, solarMonth: Int, solarDay: Int): String {
        val specialDays = getSpecialDays(solarYear, solarMonth, solarDay)
        if (specialDays.isEmpty()) return ""

        // If there's a solar term, prefer showing it; otherwise show first festival
        val term = specialDays.firstOrNull { it.type == SpecialDayType.SOLAR_TERM }
        if (term != null) return term.name

        return specialDays.first().name
    }

    // --- Lunar Festivals (month, day, isLeap) ---

    private fun getLunarFestival(lunarMonth: Int, lunarDay: Int, isLeap: Boolean): String? {
        if (isLeap) return null // No festivals in leap months

        return when {
            lunarMonth == 1 && lunarDay == 1 -> "春节"
            lunarMonth == 1 && lunarDay == 15 -> "元宵节"
            lunarMonth == 2 && lunarDay == 2 -> "龙抬头"
            lunarMonth == 5 && lunarDay == 5 -> "端午节"
            lunarMonth == 7 && lunarDay == 7 -> "七夕"
            lunarMonth == 7 && lunarDay == 15 -> "中元节"
            lunarMonth == 8 && lunarDay == 15 -> "中秋节"
            lunarMonth == 9 && lunarDay == 9 -> "重阳节"
            lunarMonth == 10 && lunarDay == 1 -> "寒衣节"
            lunarMonth == 12 && lunarDay == 8 -> "腊八节"
            lunarMonth == 12 && lunarDay == 23 -> "小年(北)"
            lunarMonth == 12 && lunarDay == 24 -> "小年(南)"
            // 除夕: last day of 12th lunar month (29 or 30)
            // 除夕 is handled in getSpecialDays() using lunar year info
            else -> null
        }
    }

    // --- Gregorian Festivals (month, day) ---

    private fun getSolarFestival(month: Int, day: Int): String? {
        return when {
            month == 1 && day == 1 -> "元旦"
            month == 2 && day == 14 -> "情人节"
            month == 3 && day == 8 -> "妇女节"
            month == 3 && day == 12 -> "植树节"
            month == 4 && day == 1 -> "愚人节"
            month == 5 && day == 1 -> "劳动节"
            month == 5 && day == 4 -> "青年节"
            month == 6 && day == 1 -> "儿童节"
            month == 7 && day == 1 -> "建党节"
            month == 8 && day == 1 -> "建军节"
            month == 9 && day == 10 -> "教师节"
            month == 10 && day == 1 -> "国庆节"
            month == 12 && day == 25 -> "圣诞节"
            else -> null
        }
    }

    // --- Variable Festivals (Mother's Day, Father's Day) ---

    private fun getVariableFestival(year: Int, month: Int, day: Int): String? {
        // Mother's Day: 2nd Sunday of May
        if (month == 5) {
            val cal = GregorianCalendar(year, 4, 1) // May 1
            val firstDow = cal.get(Calendar.DAY_OF_WEEK) // 1=Sunday, 2=Monday, ...
            val firstSunday = 1 + (8 - firstDow) % 7
            val secondSunday = firstSunday + 7
            if (day == secondSunday) return "母亲节"
        }
        // Father's Day: 3rd Sunday of June
        if (month == 6) {
            val cal = GregorianCalendar(year, 5, 1) // June 1
            val firstDow = cal.get(Calendar.DAY_OF_WEEK)
            val firstSunday = 1 + (8 - firstDow) % 7
            val thirdSunday = firstSunday + 14
            if (day == thirdSunday) return "父亲节"
        }
        return null
    }

    // --- Solar Term Calculation ---

    private data class SolarTermInfo(val month: Int, val d: Double, val c20: Int, val c21: Int)

    private val solarTermData = listOf(
        SolarTermInfo(1,  0.2422, 6, 5),   // 1: 小寒
        SolarTermInfo(1,  0.2422, 21, 20), // 2: 大寒
        SolarTermInfo(2,  0.2422, 4, 3),   // 3: 立春
        SolarTermInfo(2,  0.2422, 19, 19), // 4: 雨水
        SolarTermInfo(3,  0.2422, 6, 6),   // 5: 惊蛰
        SolarTermInfo(3,  0.2422, 21, 21), // 6: 春分
        SolarTermInfo(4,  0.2422, 6, 5),   // 7: 清明
        SolarTermInfo(4,  0.2422, 21, 21), // 8: 谷雨
        SolarTermInfo(5,  0.2422, 6, 6),   // 9: 立夏
        SolarTermInfo(5,  0.2422, 22, 22), // 10: 小满
        SolarTermInfo(6,  0.2422, 6, 6),   // 11: 芒种
        SolarTermInfo(6,  0.2422, 22, 22), // 12: 夏至
        SolarTermInfo(7,  0.2422, 8, 8),   // 13: 小暑
        SolarTermInfo(7,  0.2422, 24, 24), // 14: 大暑
        SolarTermInfo(8,  0.2422, 8, 8),   // 15: 立秋
        SolarTermInfo(8,  0.2422, 24, 24), // 16: 处暑
        SolarTermInfo(9,  0.2422, 8, 8),   // 17: 白露
        SolarTermInfo(9,  0.2422, 24, 24), // 18: 秋分
        SolarTermInfo(10, 0.2422, 9, 9),   // 19: 寒露
        SolarTermInfo(10, 0.2422, 24, 24), // 20: 霜降
        SolarTermInfo(11, 0.2422, 8, 8),   // 21: 立冬
        SolarTermInfo(11, 0.2422, 23, 23), // 22: 小雪
        SolarTermInfo(12, 0.2422, 8, 8),   // 23: 大雪
        SolarTermInfo(12, 0.2422, 22, 23), // 24: 冬至
    )

    private val solarTermNames = listOf(
        "小寒", "大寒", "立春", "雨水", "惊蛰", "春分",
        "清明", "谷雨", "立夏", "小满", "芒种", "夏至",
        "小暑", "大暑", "立秋", "处暑", "白露", "秋分",
        "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"
    )

    /**
     * Returns the solar term name for the given Gregorian date, or null if none.
     */
    private fun getSolarTerm(year: Int, month: Int, day: Int): String? {
        val y = if (year >= 2000) year - 2000 else year - 1900
        val is21c = year >= 2000

        for (i in solarTermData.indices) {
            val info = solarTermData[i]
            if (info.month != month) continue

            val c = if (is21c) info.c21 else info.c20
            val expectedDay = (y * info.d + c).toInt() - y / 4
            if (expectedDay == day) {
                return solarTermNames[i]
            }
        }
        return null
    }
}