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
}