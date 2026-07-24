package com.example.ld_calender

import android.content.Context
import android.os.Environment
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.util.Scanner

/**
 * Persistent storage for daily plans using a JSON file.
 *
 * Data is stored in two locations:
 * 1. Internal: context.filesDir/plans.json — primary storage
 * 2. Public: Documents/LDCalendar/plans.json — survives app uninstall
 *
 * On every save, both copies are updated.
 * On first launch, if internal copy is missing but public backup exists, it is restored.
 */
object PlanStorage {

    private const val FILE_NAME = "plans.json"
    private const val BACKUP_DIR_NAME = "LDCalendar"

    /**
     * Primary internal storage: survives app upgrades, deleted on uninstall.
     */
    private fun getInternalFile(context: Context): File {
        return File(context.filesDir, FILE_NAME)
    }

    /**
     * Public backup storage: survives uninstall (user-visible in Documents folder).
     */
    private fun getBackupFile(): File? {
        return try {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val backupDir = File(dir, BACKUP_DIR_NAME)
            if (!backupDir.exists()) backupDir.mkdirs()
            File(backupDir, FILE_NAME)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Initialize: if internal storage is empty but public backup exists, restore from it.
     * Call this once at app startup.
     */
    fun initialize(context: Context) {
        val internalFile = getInternalFile(context)
        if (!internalFile.exists()) {
            val backupFile = getBackupFile()
            if (backupFile != null && backupFile.exists()) {
                try {
                    backupFile.copyTo(internalFile, overwrite = true)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * Write JSON content to both internal and public backup files.
     */
    private fun writeBoth(context: Context, json: String) {
        // Write internal
        try {
            FileWriter(getInternalFile(context)).use { it.write(json) }
        } catch (_: Exception) {}

        // Write public backup
        try {
            val backupFile = getBackupFile()
            if (backupFile != null) {
                FileWriter(backupFile).use { it.write(json) }
            }
        } catch (_: Exception) {}
    }

    /**
     * Load all plans from internal storage. Returns empty map if file doesn't exist.
     */
    fun loadAll(context: Context): MutableMap<String, String> {
        val file = getInternalFile(context)
        if (!file.exists()) return mutableMapOf()
        return try {
            val json = Scanner(file).useDelimiter("\\A").next()
            val obj = JSONObject(json)
            val map = mutableMapOf<String, String>()
            obj.keys().forEach { key -> map[key] = obj.getString(key) }
            map
        } catch (e: Exception) {
            mutableMapOf()
        }
    }

    /**
     * Save all plans to both internal and public backup.
     */
    fun saveAll(context: Context, plans: Map<String, String>) {
        try {
            val obj = JSONObject()
            plans.forEach { (key, value) -> obj.put(key, value) }
            writeBoth(context, obj.toString(2))
        } catch (_: Exception) {}
    }

    /**
     * Get plan for a specific date. Returns empty string if none exists.
     */
    fun getPlan(context: Context, dateKey: String): String {
        return loadAll(context)[dateKey] ?: ""
    }

    /**
     * Set plan for a specific date.
     */
    fun setPlan(context: Context, dateKey: String, text: String) {
        val all = loadAll(context)
        all[dateKey] = text
        saveAll(context, all)
    }

    /**
     * Delete plan for a specific date.
     */
    fun deletePlan(context: Context, dateKey: String) {
        val all = loadAll(context)
        all.remove(dateKey)
        saveAll(context, all)
    }

    /**
     * Delete all plans (clear both internal and backup).
     */
    fun deleteAll(context: Context) {
        val internal = getInternalFile(context)
        if (internal.exists()) internal.delete()
        try {
            val backup = getBackupFile()
            if (backup != null && backup.exists()) backup.delete()
        } catch (_: Exception) {}
    }

    /**
     * Export all plans to a JSON string (for sharing).
     */
    fun exportAll(context: Context): String {
        val file = getInternalFile(context)
        return if (file.exists()) {
            Scanner(file).useDelimiter("\\A").next()
        } else {
            "{}"
        }
    }

    /**
     * Import plans from a JSON string.
     */
    fun importAll(context: Context, json: String) {
        try {
            val obj = JSONObject(json)
            val all = mutableMapOf<String, String>()
            obj.keys().forEach { key -> all[key] = obj.getString(key) }
            saveAll(context, all)
        } catch (_: Exception) {}
    }
}