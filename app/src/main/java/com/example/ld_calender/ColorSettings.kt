package com.example.ld_calender

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.util.Scanner
import kotlin.math.abs

/**
 * Persists the user-selected font color and background color
 * using a JSON settings file (settings.json).
 */
object ColorSettings {
    private const val FILE_NAME = "settings.json"
    private const val KEY_FONT_COLOR = "font_color"
    private const val KEY_BG_COLOR = "bg_color"

    /** Default font color: deep green (ARGB int). */
    const val DEFAULT_FONT_COLOR = 0xFF2E7D32

    /** Default background color: light gray (ARGB int). */
    const val DEFAULT_BG_COLOR = 0xFFF7F8FA

    private fun getFile(context: Context): File {
        return File(context.filesDir, FILE_NAME)
    }

    /** Loads the JSON settings object. Returns an empty object if the file doesn't exist or is corrupt. */
    private fun loadJson(context: Context): JSONObject {
        val file = getFile(context)
        if (!file.exists()) return JSONObject()
        return try {
            val json = Scanner(file).useDelimiter("\\A").next()
            JSONObject(json)
        } catch (e: Exception) {
            JSONObject()
        }
    }

    /** Writes the JSON settings object to the settings file. */
    private fun saveJson(context: Context, obj: JSONObject) {
        try {
            FileWriter(getFile(context)).use { it.write(obj.toString(2)) }
        } catch (_: Exception) {}
    }

    /**
     * Reads the persisted font color.
     * If nothing has been saved yet, returns the default deep green.
     *
     * 颜色以 ARGB Int（即 [Color.toArgb] 的返回格式）保存，
     * 因此读取时使用 [Color] 的 Int 构造函数即可还原原色。
     * （注意：不能使用 [Color] 的 Long 构造函数，
     *   否则会把 ARGB Int 按 ARGB8888 的 Long 高位格式错误解析。）
     */
    fun getFontColor(context: Context): Color {
        val obj = loadJson(context)
        return if (obj.has(KEY_FONT_COLOR)) {
            // Color(Int) 与 toArgb() 配对，可完整还原颜色（含透明度）
            runCatching { Color(obj.getLong(KEY_FONT_COLOR).toInt()) }
                .getOrDefault(Color(DEFAULT_FONT_COLOR))
        } else {
            Color(DEFAULT_FONT_COLOR)
        }
    }

    /**
     * Reads the persisted background color.
     * If nothing has been saved yet, returns the default light gray.
     */
    fun getBgColor(context: Context): Color {
        val obj = loadJson(context)
        return if (obj.has(KEY_BG_COLOR)) {
            runCatching { Color(obj.getLong(KEY_BG_COLOR).toInt()) }
                .getOrDefault(Color(DEFAULT_BG_COLOR))
        } else {
            Color(DEFAULT_BG_COLOR)
        }
    }

    fun setFontColor(context: Context, color: Color) {
        val obj = loadJson(context)
        // 使用 toArgb() 把颜色转换为 ARGB Int 后保存，保证与读取端 Color(Int) 兼容
        obj.put(KEY_FONT_COLOR, color.toArgb().toLong())
        saveJson(context, obj)
    }

    fun setBgColor(context: Context, color: Color) {
        val obj = loadJson(context)
        obj.put(KEY_BG_COLOR, color.toArgb().toLong())
        saveJson(context, obj)
    }

    /** 判断两个颜色是否相同或过于接近（导致文字与背景难以区分）。 */
    private fun colorsTooSimilar(c1: Color, c2: Color, threshold: Float = 0.12f): Boolean {
        return abs(c1.red - c2.red) < threshold &&
                abs(c1.green - c2.green) < threshold &&
                abs(c1.blue - c2.blue) < threshold
    }

    /**
     * Loads the full color settings as an [AppColors] instance.
     *
     * 程序运行后检查字体颜色/背景颜色的预设值：
     * 1. 若预设值为空（settings.json 中缺少对应键），
     *    则自动赋予默认值（字体=深绿，背景=浅灰）并写回文件。
     * 2. 若字体颜色与背景颜色相同或过于接近（导致文字不可见），
     *    则把字体颜色替换为默认深绿色并写回文件。
     */
    fun load(context: Context): AppColors {
        val obj = loadJson(context)

        var fontColor = if (obj.has(KEY_FONT_COLOR)) {
            runCatching { Color(obj.getLong(KEY_FONT_COLOR).toInt()) }
                .getOrDefault(Color(DEFAULT_FONT_COLOR))
        } else {
            // 预设值为空：赋予字体绿色
            Color(DEFAULT_FONT_COLOR)
        }

        val bgColor = if (obj.has(KEY_BG_COLOR)) {
            runCatching { Color(obj.getLong(KEY_BG_COLOR).toInt()) }
                .getOrDefault(Color(DEFAULT_BG_COLOR))
        } else {
            // 预设值为空：赋予默认背景色
            Color(DEFAULT_BG_COLOR)
        }

        var needsSave = !obj.has(KEY_FONT_COLOR) || !obj.has(KEY_BG_COLOR)

        // 若字体颜色与背景颜色相同或过于接近（文字不可见），
        // 则将字体颜色替换为默认深绿色，确保文字清晰可见
        if (colorsTooSimilar(fontColor, bgColor)) {
            fontColor = Color(DEFAULT_FONT_COLOR)
            needsSave = true
        }

        // 发生任何修正时，把有效值写回文件，保证下次启动能读到正确颜色
        if (needsSave) {
            obj.put(KEY_FONT_COLOR, fontColor.toArgb().toLong())
            obj.put(KEY_BG_COLOR, bgColor.toArgb().toLong())
            saveJson(context, obj)
        }

        return AppColors(fontColor = fontColor, bgColor = bgColor)
    }

    /** Persists the full color settings. */
    fun save(context: Context, colors: AppColors) {
        setFontColor(context, colors.fontColor)
        setBgColor(context, colors.bgColor)
    }
}