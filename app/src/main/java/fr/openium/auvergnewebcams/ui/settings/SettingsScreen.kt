package fr.openium.auvergnewebcams.ui.settings


import android.content.Intent
import android.provider.Settings
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.getAppVersion
import fr.openium.auvergnewebcams.ui.core.AWTopBar
import fr.openium.auvergnewebcams.ui.ext.navigateToLink
import fr.openium.auvergnewebcams.ui.navigation.Destination
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.utils.AnalyticsUtils
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
    vm: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    navigateTo: (Destination) -> Unit,
) {

    val context = LocalContext.current

    val scrollState = rememberScrollState()

    val qualityHighEnabled by vm.isWebcamsHighQuality.collectAsState()

    var showWebcamDialog by remember { mutableStateOf(false) }

    var showOpenSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        backgroundColor = AWAppTheme.colors.greyVeryDark,
        topBar =
        {
            AWTopBar(
                title = stringResource(R.string.settings_title),
                onNavigateBack = onNavigateBack,
                onNavigateTo = { },
                onNavigateToOpt = { },
                isPrimaryButton = false,
                isOptionalButton = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.grey_dark))
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 24.dp)
                    .navigationBarsPadding()
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
                            event.action == MotionEvent.ACTION_HOVER_EXIT
                        }
                        .clickable { vm.onQualityChanged(!qualityHighEnabled, context) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_global_quality_high),
                        style = AWAppTheme.typography.p1,
                        color = AWAppTheme.colors.white,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = qualityHighEnabled,
                        onCheckedChange = { isChecked -> vm.onQualityChanged(isChecked, context) }
                    )
                }

                SettingItem(textResId = R.string.settings_notifications) {
                    showOpenSettingsDialog = true
                }

                if (showOpenSettingsDialog) {
                    ConfirmOpenSettingsDialog(
                        onDismiss = { showOpenSettingsDialog = false },
                        onConfirm = {
                            showOpenSettingsDialog = false
                            context.startActivity(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                }
                            )
                        }
                    )
                }

                Text(
                    text = stringResource(R.string.settings_credits_title).uppercase(),
                    style = AWAppTheme.typography.p3,
                    color = colorResource(id = R.color.grey),
                    modifier = Modifier.padding(top = 32.dp, bottom = 12.dp)
                )

                SettingItem(textResId = R.string.settings_credits_about) {
                    AnalyticsUtils.aboutClicked(context)
                    navigateTo(Destination.About)
                }
                SettingItem(textResId = R.string.settings_credits_openium) {
                    AnalyticsUtils.websiteOpeniumClicked(context)
                    context.navigateToLink(context.getString(R.string.url_openium).toUri())
                }

                SettingItem(textResId = R.string.settings_send_new_webcam) {
                    AnalyticsUtils.suggestWebcamClicked(context)
                    showWebcamDialog = true
                }

                SettingItem(textResId = R.string.settings_credits_note) {
                    AnalyticsUtils.rateAppClicked(context)
                    context.navigateToLink(
                        context.getString(
                            R.string.url_note_format,
                            context.packageName
                        ).toUri()
                    )

                }

                Text(
                    text = context.getAppVersion(),
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
                            vm.sendEmail(context)
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
        }
    )

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingItem(textResId: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .pointerInteropFilter { event ->
                event.action == MotionEvent.ACTION_HOVER_EXIT
            }
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = textResId),
            style = AWAppTheme.typography.p1,
            color = colorResource(id = R.color.selector_color_white_to_grey),
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right_small),
            contentDescription = null,
            tint = AWAppTheme.colors.white

        )
    }
}


@Composable
private fun ConfirmOpenSettingsDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(modifier = Modifier.background(color = AWAppTheme.colors.greyVeryDark)) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(12.dp),
                backgroundColor = AWAppTheme.colors.greyVeryDark,
                contentColor = AWAppTheme.colors.white,
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_notifications_dialog), style = AWAppTheme.typography.p3,
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(text = stringResource(R.string.generic_cancel), color = AWAppTheme.colors.white, style = AWAppTheme.typography.p3)
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = onConfirm) {
                            Text(text = stringResource(R.string.generic_ok), color = AWAppTheme.colors.white, style = AWAppTheme.typography.p3)
                        }
                    }
                }
            }
        }
    }
}




