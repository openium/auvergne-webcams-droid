package fr.openium.auvergnewebcams.ui.settings.components

import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

private const val MIN_VALUE = 1
private const val MAX_VALUE = 120

@Composable
fun SettingsRefreshDelayDialog(
    currentValue: Int,
    onClickCancel: () -> Unit,
    onValidateNewValue: (Int) -> Unit
) {
    var valueSelected by remember {
        mutableStateOf(currentValue)
    }

    Dialog(
        onDismissRequest = onClickCancel
    ) {
        Column(
            modifier = Modifier
                .background(color = AWAppTheme.colors.black)
                .wrapContentSize()
                .padding(24.dp)
        ) {
            AndroidView(modifier = Modifier.align(Alignment.CenterHorizontally),
                factory = { context ->
                    val picker = NumberPicker(context).apply {
                        minValue = MIN_VALUE
                        maxValue = MAX_VALUE
                        value = valueSelected
                    }
                    picker.setOnValueChangedListener { _, _, newVal ->
                        valueSelected = newVal
                    }
                    picker
                })
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = onClickCancel,
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = AWAppTheme.colors.white
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.generic_cancel),
                        style = AWAppTheme.typography.button
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(
                    onClick = {
                        onValidateNewValue(valueSelected)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = AWAppTheme.colors.white
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.generic_ok),
                        style = AWAppTheme.typography.button
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun SettingsRefreshDelayDialogPreview() {
    AWTheme {
        SettingsRefreshDelayDialog(
            currentValue = 2,
            onClickCancel = {},
            onValidateNewValue = {}
        )
    }
}