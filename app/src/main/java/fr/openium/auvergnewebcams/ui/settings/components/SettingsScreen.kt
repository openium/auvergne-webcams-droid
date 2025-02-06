package fr.openium.auvergnewebcams.ui.settings.components


import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.settings.SettingsViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(vm: SettingsViewModel = koinViewModel()) {

    val version = vm.getAppVersion()

    val scrollState = rememberScrollState()

    var refreshEnabled by remember { mutableStateOf(false) }
    val interactionSourceRefresh = remember { MutableInteractionSource() }

    var qualityHighEnabled by remember { mutableStateOf(false) }
    val interactionSourceQuality = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.grey_dark))
            .verticalScroll(scrollState)
            .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_global_title).uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
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
                ) { refreshEnabled = !refreshEnabled },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_global_refresh),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = refreshEnabled,
                onCheckedChange = { refreshEnabled = it },
            )
        }

        if (refreshEnabled) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_global_refresh_delay),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(id = R.color.selector_color_white_to_grey),
                    modifier = Modifier.weight(1f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "10",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
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
                ) { qualityHighEnabled = !qualityHighEnabled },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_global_quality_high),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = qualityHighEnabled,
                onCheckedChange = { qualityHighEnabled = it },
            )
        }

        Text(
            text = stringResource(R.string.settings_credits_title).uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = colorResource(id = R.color.grey),
            modifier = Modifier.padding(top = 32.dp, bottom = 12.dp)
        )

        SettingItem(textResId = R.string.settings_credits_about) { }
        SettingItem(textResId = R.string.settings_credits_openium) { }
        SettingItem(textResId = R.string.settings_credits_pirates) { }
        SettingItem(textResId = R.string.settings_send_new_webcam) { }
        SettingItem(textResId = R.string.settings_credits_note) { }

        Text(
            text = version,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = colorResource(id = R.color.grey),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .wrapContentWidth(Alignment.End)
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
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


