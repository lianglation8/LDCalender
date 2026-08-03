package com.example.ld_calender

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.util.Scanner
import java.util.UUID

/**
 * Persistent storage for user-defined lunar festivals.
 *
 * Each festival has:
 * - id: unique identifier (UUID)
 * - lunarMonth: 1-12
 * - lunarDay: 1-30
 * - name: festival name
 * - includeLeap: whether to also show in leap months
 */
object CustomFestivalStorage {

    private const val FILE_NAME = "custom_festivals.json"

    data class CustomFestival(
        val id: String = UUID.randomUUID().toString(),
        val lunarMonth: Int,
        val lunarDay: Int,
        val name: String,
        val includeLeap: Boolean = false
    )

    private fun getFile(context: Context): File {
        return File(context.filesDir, FILE_NAME)
    }

    /**
     * Load all custom festivals from storage.
     */
    fun loadAll(context: Context): List<CustomFestival> {
        val file = getFile(context)
        if (!file.exists()) return emptyList()
        return try {
            val json = Scanner(file).useDelimiter("\\A").next()
            val array = JSONArray(json)
            val list = mutableListOf<CustomFestival>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CustomFestival(
                        id = obj.getString("id"),
                        lunarMonth = obj.getInt("lunarMonth"),
                        lunarDay = obj.getInt("lunarDay"),
                        name = obj.getString("name"),
                        includeLeap = obj.optBoolean("includeLeap", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Save all custom festivals to storage.
     */
    fun saveAll(context: Context, festivals: List<CustomFestival>) {
        try {
            val array = JSONArray()
            festivals.forEach { f ->
                val obj = JSONObject()
                obj.put("id", f.id)
                obj.put("lunarMonth", f.lunarMonth)
                obj.put("lunarDay", f.lunarDay)
                obj.put("name", f.name)
                obj.put("includeLeap", f.includeLeap)
                array.put(obj)
            }
            FileWriter(getFile(context)).use { it.write(array.toString(2)) }
        } catch (_: Exception) {}
    }

    /**
     * Add a new custom festival. Returns the new list.
     */
    fun add(context: Context, festival: CustomFestival): List<CustomFestival> {
        val all = loadAll(context).toMutableList()
        all.add(festival)
        saveAll(context, all)
        return all
    }

    /**
     * Update an existing custom festival by id. Returns the new list.
     */
    fun update(context: Context, updated: CustomFestival): List<CustomFestival> {
        val all = loadAll(context).toMutableList()
        val index = all.indexOfFirst { it.id == updated.id }
        if (index >= 0) {
            all[index] = updated
            saveAll(context, all)
        }
        return all
    }

    /**
     * Delete a custom festival by id. Returns the new list.
     */
    fun delete(context: Context, id: String): List<CustomFestival> {
        val all = loadAll(context).toMutableList()
        all.removeAll { it.id == id }
        saveAll(context, all)
        return all
    }
}