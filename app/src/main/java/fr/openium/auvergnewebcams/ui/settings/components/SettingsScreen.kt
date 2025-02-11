package fr.openium.auvergnewebcams.ui.settings.components


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.chargemap.compose.numberpicker.NumberPicker
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.settings.NavigationEvent
import fr.openium.auvergnewebcams.ui.settings.SettingsViewModel
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(vm: SettingsViewModel = koinViewModel()) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.ToActivity -> {
                    val intent = Intent(context, event.activityClass.java)
                    context.startActivity(intent)
                }

                is NavigationEvent.ToUrl -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
                    context.startActivity(intent)
                }
            }
        }
    }

    val version = vm.getAppVersion()

    val scrollState = rememberScrollState()

    val interactionSourceRefresh = remember { MutableInteractionSource() }
    val interactionSourceIntRefresh = remember { MutableInteractionSource() }
    val interactionSourceQuality = remember { MutableInteractionSource() }

    val qualityHighEnabled = vm.isWebcamsHighQuality
    val isDelayRefreshActive = vm.isDelayRefreshActive

    var showDelayDialog by remember { mutableStateOf(false) }
    var showWebcamDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.grey_dark))
            .verticalScroll(scrollState)
            .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_global_title).uppercase(),
            style = AWAppTheme.typography.p3,
            color = colorResource(id = R.color.grey),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInteropFilter { event ->
                    (event.action == MotionEvent.ACTION_HOVER_EXIT)
                }
                .clickable(
                    indication = null,
                    interactionSource = interactionSourceRefresh
                ) { vm.onDelayRefreshChanged(!isDelayRefreshActive) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_global_refresh),
                style = AWAppTheme.typography.p1,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isDelayRefreshActive,
                onCheckedChange = { vm.onDelayRefreshChanged(it) },
            )
        }

        if (isDelayRefreshActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
                    .clickable(
                        indication = null,
                        interactionSource = interactionSourceIntRefresh
                    ) { showDelayDialog = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_global_refresh_delay),
                    style = AWAppTheme.typography.p1,
                    color = colorResource(id = R.color.selector_color_white_to_grey),
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = vm.refreshDelay.toString(),
                        style = AWAppTheme.typography.p1,
                        color = colorResource(id = R.color.selector_color_white_to_grey)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInteropFilter { event ->
                    event.action == MotionEvent.ACTION_HOVER_EXIT
                }
                .clickable(
                    indication = null,
                    interactionSource = interactionSourceQuality
                ) { vm.onQualityChanged(!qualityHighEnabled) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_global_quality_high),
                style = AWAppTheme.typography.p1,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = qualityHighEnabled,
                onCheckedChange = { isChecked -> vm.onQualityChanged(isChecked) }
            )
        }

        Text(
            text = stringResource(R.string.settings_credits_title).uppercase(),
            style = AWAppTheme.typography.p3,
            color = colorResource(id = R.color.grey),
            modifier = Modifier.padding(top = 32.dp, bottom = 12.dp)
        )

        SettingItem(textResId = R.string.settings_credits_about) {
            vm.onAboutClicked()
        }
        SettingItem(textResId = R.string.settings_credits_openium) {
            vm.onOpeniumClicked()
        }
        SettingItem(textResId = R.string.settings_credits_pirates) {
            vm.onLesPiratesClicked()
        }

        SettingItem(textResId = R.string.settings_send_new_webcam) {
            showWebcamDialog = true
        }

        SettingItem(textResId = R.string.settings_credits_note) {
            vm.onRateClicked()
        }

        Text(
            text = version,
            style = AWAppTheme.typography.p3,
            color = colorResource(id = R.color.grey),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .wrapContentWidth(Alignment.End)
        )
    }

    if (showWebcamDialog) {
        AlertDialog(
            onDismissRequest = { showWebcamDialog = false },
            title = {
                Text(text = stringResource(id = R.string.settings_send_new_webcam_title), style = AWAppTheme.typography.p1)
            },
            text = {
                Text(text = stringResource(id = R.string.settings_send_new_webcam_message), style = AWAppTheme.typography.p1)
            },
            confirmButton = {
                TextButton(onClick = {
                    AnalyticsUtils.suggestWebcamClicked(context)
                    sendEmail(context)
                    showWebcamDialog = false
                }) {
                    Text(text = stringResource(id = R.string.generic_ok), style = AWAppTheme.typography.p1)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWebcamDialog = false }) {
                    Text(text = stringResource(id = R.string.generic_cancel), style = AWAppTheme.typography.p1)
                }
            }
        )
    }

    if (showDelayDialog) {
        RefreshDelayPickerDialog(
            currentDelay = vm.refreshDelay,
            onDismiss = { showDelayDialog = false },
            onConfirm = { newDelay ->
                vm.onRefreshDelayChanged(newDelay)
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingItem(textResId: Int, onClick: () -> Unit) {
    val interactionSourceItem = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .pointerInteropFilter { event ->
                event.action == MotionEvent.ACTION_HOVER_EXIT
            }
            .clickable(
                indication = null,
                interactionSource = interactionSourceItem
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = textResId),
            style = AWAppTheme.typography.p1,
            color = colorResource(id = R.color.selector_color_white_to_grey),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White
        )
    }
}

fun sendEmail(context: Context) {
    val intentEmail = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:${context.getString(R.string.detail_signal_problem_email)}")
        putExtra(
            Intent.EXTRA_SUBJECT,
            context.getString(R.string.settings_send_new_webcam_email_title)
        )
        putExtra(
            Intent.EXTRA_TEXT,
            context.getString(R.string.settings_send_new_webcam_email_message)
        )
    }
    val chooser = Intent.createChooser(intentEmail, context.getString(R.string.generic_chooser))
    if (chooser.resolveActivity(context.packageManager) != null) {
        context.startActivity(chooser)
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.generic_no_email_app),
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun RefreshDelayPickerDialog(
    currentDelay: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedDelay by remember { mutableStateOf(currentDelay) }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                NumberPicker(
                    value = selectedDelay,
                    range = 1..120,
                    onValueChange = { selectedDelay = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(id = R.string.generic_cancel), style = AWAppTheme.typography.p1)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        onConfirm(selectedDelay)
                        onDismiss()
                    }) {
                        Text(text = stringResource(id = R.string.generic_ok), style = AWAppTheme.typography.p1)
                    }
                }
            }
        }
    }
}





