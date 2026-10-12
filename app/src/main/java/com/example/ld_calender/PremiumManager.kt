package com.example.ld_calender

import android.content.Context
import android.content.SharedPreferences

/**
 * 付费（去广告）状态管理。
 *
 * 目前使用 SharedPreferences 本地标记保存"是否已付费"状态。
 * 将来接入 Google Play Billing 后，可在购买成功回调中调用 [setPremium]，
 * 并在应用启动时通过 BillingClient.queryPurchases() 校验恢复购买。
 */
object PremiumManager {

    private const val PREFS_NAME = "premium_prefs"
    private const val KEY_IS_PREMIUM = "is_premium"

    private lateinit var prefs: SharedPreferences

    /** 初始化必须在应用启动时调用一次（例如 MainActivity.onCreate） */
    fun initialize(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /** 是否已付费去广告 */
    val isPremium: Boolean
        get() {
            if (!::prefs.isInitialized) return false
            return prefs.getBoolean(KEY_IS_PREMIUM, false)
        }

    /** 标记为已付费（购买成功后调用） */
    fun setPremium(value: Boolean) {
        if (!::prefs.isInitialized) return
        prefs.edit().putBoolean(KEY_IS_PREMIUM, value).apply()
    }
}