package fr.openium.auvergnewebcams.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme
import fr.openium.auvergnewebcams.ui.theme.AWTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchItem(
    currentSearch: String,
    onSearchChange: (String) -> Unit,
    clearSearch: () -> Unit,
    focusRequester: FocusRequester
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = currentSearch,
        onValueChange = onSearchChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(color = AWAppTheme.colors.grey)
            .focusRequester(focusRequester = focusRequester),
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = stringResource(id = R.string.search_hint)
            )
        }, placeholder = {
            Text(
                text = stringResource(id = R.string.search_hint),
                color = AWAppTheme.colors.greyLight,
                style = AWAppTheme.typography.p1Italic
            )
        },
        maxLines = 1,
        textStyle = AWAppTheme.typography.p1Italic,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = AWAppTheme.colors.grey,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = AWAppTheme.colors.greyLight,
            textColor = AWAppTheme.colors.white,
            leadingIconColor = AWAppTheme.colors.white,
            placeholderColor = AWAppTheme.colors.greyLight,
            focusedLabelColor = AWAppTheme.colors.white
        ),
        trailingIcon = {
            if (currentSearch.isNotBlank()) {
                IconButton(onClick = clearSearch) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.search_hint),
                        tint = AWAppTheme.colors.white
                    )
                }
            }
        },
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Ascii)
    )
}

@Composable
@Preview
fun SearchItemPreview() {
    AWTheme {
        SearchItem(
            currentSearch = "",
            onSearchChange = {},
            clearSearch = {},
            focusRequester = remember { FocusRequester() }
        )
    }
}