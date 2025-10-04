package com.example.spendless.features.finance.database.mapper

import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.database.model.TransactionEntity

fun TransactionEntity.toTransactionItem(): TransactionItem {
    return TransactionItem(
        category = category,
        isExpense = isExpense,
        title = title,
        description = description,
        content = content,
        image = image,
        price = price,
        date = date
    )
}

fun TransactionItem.toTransactionEntity(): TransactionEntity{
    return TransactionEntity(
        category = category,
        isExpense = isExpense,
        title = title,
        description = description,
        content = content,
        image = image,
        price = price,
        date = date
    )
}