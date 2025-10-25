package com.ghostdev.storekeeperhng.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

object Formatters {
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "NG"))

    fun formatCurrency(amount: Double): String {
        return currencyFormat.format(amount)
    }

    fun timeAgo(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return when {
            days > 0 -> "$days day${if (days == 1L) "" else "s"} ago"
            hours > 0 -> "$hours hour${if (hours == 1L) "" else "s"} ago"
            minutes > 0 -> "$minutes minute${if (minutes == 1L) "" else "s"} ago"
            else -> "Just now"
        }
    }
}