package fr.openium.auvergnewebcams.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.core.AWDialogMessage
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme

@Composable
fun SettingsScreen(
    isWebcamsDelayRefreshActive: Boolean,
    webcamsDelayRefreshValue: Int,
    isWebcamsHighQuality: Boolean,
    version: String,
    changeSettingsRefreshDelay: (Boolean) -> Unit,
    changeWebcamDelayRefreshValue: (Int) -> Unit,
    changeWebcamHighQuality: (Boolean) -> Unit,
    navigateToAbout: () -> Unit,
    navigateToOpeniumWebsite: () -> Unit,
    navigateToPiratesWebsite: () -> Unit,
    proposeNewWebcam: () -> Unit,
    rateApp: () -> Unit
) {

    var showDialogNewWebcam by remember {
        mutableStateOf(false)
    }

    var showRefreshDelayDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SettingsItemHeader(text = stringResource(id = R.string.settings_global_title))

        SettingsItemSwitch(
            text = stringResource(id = R.string.settings_global_refresh),
            isChecked = isWebcamsDelayRefreshActive,
            onCheckedChange = changeSettingsRefreshDelay
        )
        if (isWebcamsDelayRefreshActive) {
            SettingsItem(
                text = stringResource(id = R.string.settings_global_refresh_delay),
                value = webcamsDelayRefreshValue.toString(),
                onClick = {
                    showRefreshDelayDialog = true
                }
            )
        }

        SettingsItemSwitch(
            text = stringResource(id = R.string.settings_global_quality_high),
            isChecked = isWebcamsHighQuality,
            onCheckedChange = changeWebcamHighQuality
        )

        Spacer(modifier = Modifier.height(22.dp))
        SettingsItemHeader(text = stringResource(id = R.string.settings_credits_title))

        SettingsItem(
            text = stringResource(id = R.string.settings_credits_about),
            onClick = navigateToAbout
        )
        SettingsItem(
            text = stringResource(id = R.string.settings_credits_openium),
            onClick = navigateToOpeniumWebsite
        )
        SettingsItem(
            text = stringResource(id = R.string.settings_credits_pirates),
            onClick = navigateToPiratesWebsite
        )
        SettingsItem(
            text = stringResource(id = R.string.settings_send_new_webcam),
            onClick = {
                showDialogNewWebcam = true
            }
        )
        SettingsItem(
            text = stringResource(id = R.string.settings_credits_note),
            onClick = rateApp
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = version,
            style = AWAppTheme.typography.p3,
            color = AWAppTheme.colors.grey,
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.End)
        )

    }

    if (showDialogNewWebcam) {
        AWDialogMessage(
            title = stringResource(id = R.string.settings_send_new_webcam_title),
            message = stringResource(id = R.string.settings_send_new_webcam_message),
            buttonText = stringResource(id = R.string.generic_ok),
            onButtonClick = {
                showDialogNewWebcam = false
                proposeNewWebcam()
            },
            onDismiss = {
                showDialogNewWebcam = false
            }
        )
    }

    if(showRefreshDelayDialog) {
        SettingsRefreshDelayDialog(
            currentValue = webcamsDelayRefreshValue,
            onClickCancel = {
                showRefreshDelayDialog = false
            },
            onValidateNewValue = {
                showRefreshDelayDialog = false
                changeWebcamDelayRefreshValue(it)
            }
        )
    }

}