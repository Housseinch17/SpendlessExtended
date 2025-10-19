package com.example.spendless.core.presentation.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.spendless.R

object SpendLessIcons {
    val TrendingUp: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.trending_up)

    val TrendingDown: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.trending_down)

    val Settings: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.settings)

    val Notes: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.notes)

    val NavigateNext: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.navigate_next)

    val NavigateBefore: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.navigate_before)

    val Logout: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.logout)

    val FingerPrint: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.fingerprint)

    val Download: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.download)

    val Close: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.close)

    val CheckIndeterminateSmall: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.check_indeterminate_small)

    val Check: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.check)

    val Backspace: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.backspace)

    val ArrowForward: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.arrow_forward)

    val ArrowBack: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.arrow_back)

    val ArrowDropDown: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.arrow_drop_down)

    val ArrowDropUp: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.arrow_drop_up)

    val Add: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.add)

    val AccountBalanceWallet: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.account_balance_wallet)

    val EllipseOff: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.ellipse_off)

    val EllipseOn: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.ellipse_on)

    val HeaderImage: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.header_image)

    val Vector: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.vector)

    val ExportBack: ImageVector
        @Composable
        get() = ImageVector.vectorResource(R.drawable.export_back)

    val LogoutAsImage: Int = R.drawable.logout
    val Lock: Int = R.drawable.lock

    val Preferences: Int = R.drawable.preferences

    const val CLOTHING: String = "R.drawable.clothing"

    const val ACCESSORIES: String = "R.drawable.accessories"

    const val HEALTH: String = "R.drawable.health"

    const val FOOD: String = "R.drawable.food"

    const val ENTERTAINMENT: String = "R.drawable.entertainment"

    const val EDUCATION: String = "R.drawable.education"

    const val RECURRENCE: String = "R.drawable.recurrence"

    const val HOME: String = "R.drawable.home"
}