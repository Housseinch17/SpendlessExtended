package com.example.spendless.core.database.user.converters

import androidx.room.TypeConverter
import com.example.spendless.core.database.user.model.Currency
import com.example.spendless.core.database.user.model.PreferencesFormat
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class PreferencesFormatConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromPreferences(preferences: PreferencesFormat): String {
        return gson.toJson(preferences)
    }

    @TypeConverter
    fun toPreferences(json: String): PreferencesFormat {
        val type = object : TypeToken<PreferencesFormat>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromCurrency(currency: Currency): String = gson.toJson(currency)

    @TypeConverter
    fun toCurrency(json: String): Currency {
        val type = object : TypeToken<Currency>() {}.type
        return gson.fromJson(json, type)
    }
}