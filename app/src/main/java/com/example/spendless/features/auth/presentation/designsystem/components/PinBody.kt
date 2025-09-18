package com.example.spendless.features.auth.presentation.designsystem.components

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.spendless.features.auth.presentation.designsystem.Constants.FINGERPRINT
import com.example.spendless.features.auth.presentation.ui.common.PinActions
import com.example.spendless.features.auth.presentation.ui.common.BasePinUiState
import com.example.spendless.features.auth.presentation.ui.pinPrompt.PinPromptUIState

@SuppressLint("ContextCastToActivity")
@Composable
fun PinBody(
    modifier: Modifier = Modifier,
    basePinUiState: BasePinUiState,
    pinActions: (PinActions) -> Unit,
) {
    var activity: AppCompatActivity? = null
    if (basePinUiState is PinPromptUIState) {
        val context = LocalContext.current
        activity = remember(context) {
            context as? AppCompatActivity
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PinEllipses(
            modifier = Modifier,
            ellipsesList = basePinUiState.ellipsesList
        )

        SpendlessKeyboard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            keys = basePinUiState.keys,
            isEnabled = basePinUiState.isEnabled,
            enabledButtons = basePinUiState.enabledButtons,
            isBackspaceEnabled = basePinUiState.isBackspaceEnabled,
            onItemClick = { pin ->
                if (pin != FINGERPRINT) {
                    pinActions(PinActions.UpdatePin(newPin = pin))
                } else {
                    pinActions(PinActions.UpdatePin(newPin = pin, activity = activity))
                }
            }
        )
    }
}