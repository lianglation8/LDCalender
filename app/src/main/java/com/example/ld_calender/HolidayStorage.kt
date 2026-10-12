package com.example.ld_calender

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Scanner
import kotlin.concurrent.thread

/**
 * 国家法定节假日数据（方案 B：静态 JSON + 联网自动拉取 + 本地缓存）。
 *
 * 数据来源：一个静态托管的 holidays.json（GitHub Pages / Gitee Pages / 对象存储等）。
 *
 * JSON 格式示例：
 * {
 *   "2026": [
 *     { "name": "元旦",   "type": "holiday", "date": "2026-01-01" },
 *     { "name": "春节",   "type": "holiday", "from": "2026-02-16", "to": "2026-02-22" },
 *     { "name": "春节调休", "type": "workday", "date": "2026-02-14" }
 *   ]
 * }
 *
 * - type = "holiday"：法定放假，日历显示"休"
 * - type = "workday"：调休上班，日历显示"班"
 * - 单日用 "date"；连续放假可用 "from" + "to"（含首尾）
 *
 * 工作流程：
 * 1. App 启动时先同步读取本地缓存（离线也能立即显示）。
 * 2. 后台线程联网拉取最新 JSON，成功后覆盖本地缓存并回调刷新 UI。
 * 3. 网络失败则沿用上次缓存，不中断使用。
 * 4. 节假日更新只需替换托管文件内容，App 下次启动自动生效，无需重新发版。
 *
 * 【数据准备步骤】（无需从政府网站下载文件，每年只需花几分钟维护一次）
 * 1. 每年 11 月左右，国务院会发布下一年度的放假安排公告（可在 gov.cn 或新闻报道中查看）。
 * 2. 按照上方 JSON 格式，将下一年度的放假日期和调休上班日期整理成 JSON。
 * 3. 参考模板：项目 docs/holidays.example.json（已含 2025 年全年数据，可直接使用）。
 * 4. 将整理好的 JSON 上传到任意静态托管（GitHub Pages / Gitee Pages / 云存储等），
 *    得到一个可公开访问的 URL。
 * 5. 将下方 HOLIDAYS_URL 替换为该 URL 即可。此后数据更新只需替换托管文件内容，
 *    App 会在下次启动时自动拉取最新数据，无需重新发版。
 *
 * 说明：政府官网不提供现成的结构化 JSON，公开数据源中也有社区维护的项目
 * （如 GitHub 上的 holiday-cn 等），也可以直接引用它们，但自行维护更可控。
 */
object HolidayStorage {

    /**
     * TODO: 替换为你的静态托管地址。
     * 例如：https://yourname.github.io/LDCalender/holidays.json
     */
    const val HOLIDAYS_URL = "https://your-static-hosting.example.com/holidays.json"

    private const val CACHE_FILE = "holidays_cache.json"
    private const val CONNECT_TIMEOUT_MS = 8000
    private const val READ_TIMEOUT_MS = 8000

    data class HolidayEntry(
        val year: Int,
        val month: Int,
        val day: Int,
        val name: String,
        val isWorkday: Boolean // true=调休上班(班), false=法定放假(休)
    )

    private fun getCacheFile(context: Context): File = File(context.filesDir, CACHE_FILE)

    private val dateParser = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // ================= 本地缓存 =================

    /**
     * 同步读取本地缓存（App 启动时调用，保证离线可用且不会卡 UI）。
     */
    fun loadCached(context: Context): List<HolidayEntry> {
        val file = getCacheFile(context)
        if (!file.exists()) return emptyList()
        return try {
            val json = Scanner(file).useDelimiter("\\A").next()
            parseHolidays(JSONObject(json))
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * 在后台线程拉取最新数据；成功则更新缓存并回调新列表，
     * 失败则回调现有缓存。回调在主线程执行。
     */
    fun refresh(context: Context, onDone: (List<HolidayEntry>) -> Unit) {
        thread {
            val fresh = fetchRemote()
            if (fresh != null) {
                saveCache(context, fresh)
            }
            val result = fresh ?: loadCached(context)
            android.os.Handler(context.mainLooper).post {
                onDone(result)
            }
        }
    }

    private fun saveCache(context: Context, entries: List<HolidayEntry>) {
        try {
            FileWriter(getCacheFile(context)).use { writer ->
                writer.write(entriesToJson(entries))
            }
        } catch (_: Exception) {}
    }

    // ================= 查询 =================

    /**
     * 返回某日期对应的节假日标记："休"（放假）/"班"（调休上班），否则 null。
     * 若同一天同时存在放假与上班（数据冲突），优先显示"班"（调休上班更关键）。
     */
    fun getHolidayTag(entries: List<HolidayEntry>, year: Int, month: Int, day: Int): String? {
        var tag: String? = null
        for (e in entries) {
            if (e.year == year && e.month == month && e.day == day) {
                if (e.isWorkday) return "班"
                tag = "休"
            }
        }
        return tag
    }

    // ================= 网络拉取 =================

    private fun fetchRemote(): List<HolidayEntry>? {
        return try {
            val conn = URL(HOLIDAYS_URL).openConnection() as HttpURLConnection
            try {
                conn.requestMethod = "GET"
                conn.connectTimeout = CONNECT_TIMEOUT_MS
                conn.readTimeout = READ_TIMEOUT_MS
                conn.instanceFollowRedirects = true
                conn.setRequestProperty("Accept", "application/json")
                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    parseHolidays(JSONObject(body))
                } else {
                    null
                }
            } finally {
                conn.disconnect()
            }
        } catch (_: Exception) {
            null
        }
    }

    // ================= JSON 解析 / 生成 =================

    private fun parseHolidays(root: JSONObject): List<HolidayEntry> {
        val result = mutableListOf<HolidayEntry>()
        val keys = root.keys()
        while (keys.hasNext()) {
            val year = keys.next().toIntOrNull() ?: continue
            val arr = root.optJSONArray(year.toString()) ?: continue
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                val name = obj.optString("name", "")
                val isWorkday = obj.optString("type", "holiday") == "workday"

                // 单日: date = "2026-01-01"
                val single = obj.optString("date", "")
                if (single.isNotEmpty()) {
                    addEntry(result, year, single, name, isWorkday)
                }
                // 连续区间: from + to = "2026-02-16" ~ "2026-02-22"（含首尾）
                val from = obj.optString("from", "")
                val to = obj.optString("to", "")
                if (from.isNotEmpty() && to.isNotEmpty()) {
                    val start = dateParser.parse(from) ?: continue
                    val end = dateParser.parse(to) ?: continue
                    if (!end.before(start)) {
                        val cal = Calendar.getInstance().apply { time = start }
                        val endCal = Calendar.getInstance().apply { time = end }
                        while (!cal.after(endCal)) {
                            val y = cal.get(Calendar.YEAR)
                            val m = cal.get(Calendar.MONTH) + 1
                            val d = cal.get(Calendar.DAY_OF_MONTH)
                            result.add(HolidayEntry(y, m, d, name, isWorkday))
                            cal.add(Calendar.DAY_OF_MONTH, 1)
                        }
                    }
                }
            }
        }
        return result
    }

    private fun addEntry(list: MutableList<HolidayEntry>, fallbackYear: Int, dateStr: String, name: String, isWorkday: Boolean) {
        try {
            val cal = Calendar.getInstance().apply { time = dateParser.parse(dateStr) ?: throw IllegalArgumentException("无法解析日期: $dateStr") }
            list.add(
                HolidayEntry(
                    year = cal.get(Calendar.YEAR),
                    month = cal.get(Calendar.MONTH) + 1,
                    day = cal.get(Calendar.DAY_OF_MONTH),
                    name = name,
                    isWorkday = isWorkday
                )
            )
        } catch (_: Exception) {
            // 容错：忽略解析失败的条目
        }
    }

    private fun entriesToJson(entries: List<HolidayEntry>): String {
        val byYear = entries.groupBy { it.year }
        val root = JSONObject()
        byYear.forEach { (year, list) ->
            val arr = JSONArray()
            list.forEach { e ->
                val obj = JSONObject()
                obj.put("name", e.name)
                obj.put("type", if (e.isWorkday) "workday" else "holiday")
                obj.put(
                    "date",
                    String.format(Locale.US, "%04d-%02d-%02d", e.year, e.month, e.day)
                )
                arr.put(obj)
            }
            root.put(year.toString(), arr)
        }
        return root.toString(2)
    }
}