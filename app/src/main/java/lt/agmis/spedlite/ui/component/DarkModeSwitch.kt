package lt.agmis.spedlite.ui.component

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import lt.agmis.spedlite.MainActivity
import lt.agmis.spedlite.R

@Composable
fun DarkModeSwitch(modifier: Modifier = Modifier, onCheckedChange: (Boolean) -> Unit) {
    val mainActivity = LocalActivity.current as? MainActivity
    var isLightMode by remember(mainActivity) { mutableStateOf(mainActivity?.isDarkTheme?.not() ?: true) }

    val trackColor = if (isLightMode) Color(0xFFE6EAEE) else Color(0xFF444F57)
    val resolvedThumbColor = if (isLightMode) Color.White else Color.White
    val trackShape = RoundedCornerShape(26.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val offsetAnimation = animateDpAsState(targetValue = if (isLightMode) (76 - 32 - 4).dp else 4.dp)

    Box(
        modifier
            .minimumInteractiveComponentSize()
            .toggleable(
                value = isLightMode,
                onValueChange = {
                    isLightMode = !isLightMode
                    onCheckedChange(isLightMode)
                    mainActivity?.isDarkTheme = isLightMode.not()
                },
                enabled = true,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null
            )
            .size(76.dp, 40.dp)
            .background(trackColor, trackShape)
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .size(32.dp)
                    .offset {
                        IntOffset(offsetAnimation.value.roundToPx(), 0)
                    }
                    .indication(
                        interactionSource = interactionSource,
                        indication = ripple(
                            bounded = false,
                            radius = 40.dp / 2
                        )
                    )
                    .background(resolvedThumbColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(if (isLightMode) R.drawable.ic_day else R.drawable.ic_night), contentDescription = null)
        }
    }
}

