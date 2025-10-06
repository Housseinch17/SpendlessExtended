package com.example.spendless.features.finance.data.database.converters

import androidx.room.TypeConverter
import com.example.spendless.core.data.model.Category
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class TransactionsConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromCategory(category: Category): String {
        return gson.toJson(category)
    }

    @TypeConverter
    fun toCategory(json: String): Category {
        val type = object : TypeToken<Category>() {}.type
        return gson.fromJson(json, type)
    }
}