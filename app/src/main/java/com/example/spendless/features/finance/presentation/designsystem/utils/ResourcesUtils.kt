package com.example.spendless.features.finance.presentation.designsystem.utils

import com.example.spendless.R
import com.example.spendless.core.data.constant.Constants

fun String.toDrawableRes(): Int {
    return Constants.drawableResMap[this] ?: R.drawable.launcher_icon_foreground
}

fun String.toStringRes(): Int {
    return Constants.stringResMap[this] ?: R.string.app_name
}