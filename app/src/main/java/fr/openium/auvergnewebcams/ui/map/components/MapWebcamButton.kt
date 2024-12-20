package fr.openium.auvergnewebcams.ui.map.components

import android.graphics.Color.parseColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ui.theme.AWAppTheme

@Composable
fun MapWebcamButton(
    backgroundColor: String?,
    iconName: String?,
    iconDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(parseColor(backgroundColor)),
            contentColor = AWAppTheme.colors.white
        ),
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            color = AWAppTheme.colors.white
        ),
        contentPadding = PaddingValues(4.dp),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp),
        modifier = modifier.size(24.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(
                id = getMapImageDrawableIdByName(iconName)
            ),
            contentDescription = iconDescription,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Preview
@Composable
private fun MapWebcamButtonPreview() {
    MapWebcamButton(
        backgroundColor = "#004D40",
        iconName = "map-annotation-mountain",
        iconDescription = "Puy de Sancy",
        onClick = {}
    )
}

private fun getMapImageDrawableIdByName(name: String?): Int {
    return when (name) {
        "map-annotation-mountain" -> R.drawable.map_annotation_mountain
        "map-annotation-highway" -> R.drawable.map_annotation_highway
        "map-annotation-lake" -> R.drawable.map_annotation_lake
        "map-annotation-city" -> R.drawable.map_annotation_city
        "map-annotation-road" -> R.drawable.map_annotation_road
        else -> R.drawable.map_annotation_mountain
    }
}