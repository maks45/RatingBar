package com.durov.composeratebar

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@Composable
@Preview
fun RateBarPreview() {
    RateBar(
        iconSelected = R.drawable.ic_icon_selected,
        iconUnSelected = R.drawable.ic_icon_unselected
    )
}

@Composable
fun RateBar(
    modifier: Modifier = Modifier,
    @DrawableRes iconSelected: Int,
    @DrawableRes iconUnSelected: Int,
    animateChanges: Boolean = true,
    itemsCount: Int = 5,
    step: Float = .1f,
    initialRate: Float = 0f,
    onRateChanged: (Float) -> Unit = {}
) {
    RateBar(
        modifier = modifier,
        iconSelected = {
            Image(
                modifier = it,
                painter = painterResource(id = iconSelected),
                contentScale = ContentScale.None,
                alignment = Alignment.CenterStart,
                contentDescription = "icon selected"
            )
        },
        iconUnSelected = {
            Image(
                modifier = it,
                painter = painterResource(id = iconUnSelected),
                contentScale = ContentScale.None,
                alignment = Alignment.CenterStart,
                contentDescription = "icon unselected"
            )
        },
        animateChanges = animateChanges,
        itemsCount = itemsCount,
        step = step,
        initialRate = initialRate,
        onRateChanged = onRateChanged
    )
}

@Composable
fun RateBar(
    modifier: Modifier = Modifier,
    iconSelected: @Composable (Modifier) -> Unit,
    iconUnSelected: @Composable (Modifier) -> Unit,
    animateChanges: Boolean = true,
    animationDuration: Int = 300,
    itemsCount: Int = 5,
    step: Float = .5f,
    initialRate: Float = 0f,
    onRateChanged: (Float) -> Unit = {}
) {
    val currentRate = remember { mutableStateOf(initialRate) }
    val animatedRate by animateFloatAsState(
        targetValue = currentRate.value,
        visibilityThreshold = 0.5f,
        animationSpec = tween(durationMillis = animationDuration, easing = LinearEasing),
    )

    Box(modifier = modifier
        .pointerInput(Unit) {
            val widgetWidth = this.size.width
            detectTapGestures { offset ->
                val rate = ((offset.x / widgetWidth) * itemsCount)
                    .roundToStep(step)
                currentRate.value = rate
                onRateChanged.invoke(rate)
            }
        }) {
        DrawRow(
            quantity = if (animateChanges) animatedRate else currentRate.value,
            total = itemsCount,
            content = iconSelected,
            contentRest = iconUnSelected
        )
    }
}

@Composable
private fun DrawRow(
    quantity: Float,
    total: Int,
    content: @Composable (Modifier) -> Unit,
    contentRest: @Composable (Modifier) -> Unit
) {
    Row {
        for (i in 1..quantity.toInt()) {
            content.invoke(Modifier)
        }
        DrawPart(
            percent = quantity - quantity.toInt(),
            right = content,
            left = contentRest
        )
        for (i in 1..(total - quantity).toInt()) {
            contentRest.invoke(Modifier)
        }
    }
}

@Composable
private fun DrawPart(
    percent: Float,
    right: @Composable (Modifier) -> Unit,
    left: @Composable (Modifier) -> Unit
) {
    if (percent > 0) {
        Box {
            right.invoke(Modifier.clipTo(percent, true))
            left.invoke(Modifier.clipTo(percent, false))
        }
    }
}

private fun Modifier.clipTo(part: Float, isRight: Boolean) = this.drawWithContent {
    if (isRight) {
        clipRect(right = size.width * part, bottom = size.height) {
            this@drawWithContent.drawContent()
        }
    } else {
        clipRect(left = size.width * part, bottom = size.height) {
            this@drawWithContent.drawContent()
        }
    }
}

private fun Float.roundToStep(step: Float) = (this * (1 / step)).roundToInt() / (1 / step)
