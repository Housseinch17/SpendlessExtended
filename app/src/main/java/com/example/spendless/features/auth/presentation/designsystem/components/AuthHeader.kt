package com.example.spendless.features.auth.presentation.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spendless.R
import com.example.spendless.core.presentation.designsystem.SpendLessIcons

@Composable
fun AuthHeader(
    modifier: Modifier = Modifier,
    header: String,
    body: AnnotatedString,
    content: @Composable ((Modifier) -> Unit),
){
    Column(
        modifier = modifier.padding(top = 36.dp, start = 26.dp, end = 26.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier,
            imageVector = SpendLessIcons.HeaderImage,
            contentDescription = stringResource(R.string.header_image)
        )
        Text(
            modifier = Modifier.padding(top = 20.dp),
            text = header,
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                letterSpacing = 0.1.sp,
                textAlign = TextAlign.Center
            ),
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = body,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        )

        //specific content for each screen
        content(Modifier.padding(top = 36.dp))

    }
}