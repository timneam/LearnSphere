package com.example.learnsphere.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.learnsphere.ui.theme.*
import kotlinx.coroutines.delay

// Reference resource: https://www.youtube.com/watch?v=xakNOVaYLAg
@Preview
@Composable
fun LoadingIndicator(
  modifier: Modifier = Modifier,
  chosenIndicator: String? = "Circular",
  type: Int? = 1,
  color: Color? = MaterialTheme.colorScheme.primary,
  trackColor: Color? = MaterialTheme.colorScheme.secondary,
  width: Dp? = 40.dp,
  startingValue: Long? = 0L,
  endingValue: Long? = 100L,
) {
  when (chosenIndicator) {
    "Custom" -> CustomIndicator(modifier = modifier, circleColor = color!!)
    "Circular" -> CircularIndicator(modifier = modifier, type = type!!, color = color!!, trackColor = trackColor!!, width = width!!)
    "Linear" -> LinearIndicator(modifier = modifier, type = type!!, color = color!!, trackColor = trackColor!!, width = width!!)
  }
}

@Composable
fun CustomIndicator(
  modifier: Modifier = Modifier,
  circleSize: Dp = 10.dp,
  circleColor: Color,
  spaceBetween: Dp = 5.dp,
  travelDistance: Dp = 20.dp,
) {
  val circles = listOf(
    remember { Animatable(initialValue = 0f) },
    remember { Animatable(initialValue = 0f) },
    remember { Animatable(initialValue = 0f) }
  )

  circles.forEachIndexed { index, animatable ->
    LaunchedEffect( key1 = animatable ) {
      delay(index * 100L)
      animatable.animateTo(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
          animation = keyframes {
            durationMillis = 1200
            0.0f at 0 with LinearOutSlowInEasing
            1.0f at 300 with LinearOutSlowInEasing
            0.0f at 600 with LinearOutSlowInEasing
            0.0f at 1200 with LinearOutSlowInEasing
          },
          repeatMode = RepeatMode.Restart
        )
      )
    }
  }

  val circleValues = circles.map { it.value }
  val distance = with(LocalDensity.current) { travelDistance.toPx() }
  val lastCircle = circleValues.size - 1

  Row(modifier = modifier) {
    circleValues.forEachIndexed { index, value ->
      Box(
        modifier = Modifier
          .size(circleSize)
          .graphicsLayer { translationY = -value * distance }
          .background(color = circleColor, shape = CircleShape)
      )

      if (index != lastCircle) {
        Spacer(modifier = Modifier.width(spaceBetween))
      }
    }
  }
}

@Composable
fun CircularIndicator(
  modifier: Modifier = Modifier,
  type: Int,
  color: Color,
  trackColor: Color,
  width: Dp
) {
  // 0 is a circular progress indicator with logic behind
  // 1 is a circular progress indicator that just keep spinning
  when (type) {
    /*0 -> CircularDeterminateIndicator(
      modifier = modifier,
      color = color,
      trackColor = trackColor,
      width = width
    )*/
    1 -> CircularProgressIndicator(
      modifier = modifier.width(width),
      color = color,
      trackColor = trackColor
    )
  }
}

@Composable
fun LinearIndicator(
  modifier: Modifier = Modifier,
  type: Int,
  color: Color,
  trackColor: Color,
  width: Dp
) {
  // 0 is a circular progress indicator with logic behind
  // 1 is a circular progress indicator that just keep spinning
  when (type) {
    /*0 -> LinearDeterminateIndicator(
      modifier = modifier,
      color = color,
      trackColor = trackColor,
      width = width
    )*/
    1 -> LinearProgressIndicator(
      modifier = modifier.width(width),
      color = color,
      trackColor = trackColor
    )
  }
}

@Composable
fun CircularDeterminateIndicator (
  modifier: Modifier = Modifier,
  color: Color,
  trackColor: Color,
  width: Dp
) {
  var currentProgress by remember { mutableStateOf(0f) }
  val scope = rememberCoroutineScope() // Create a coroutine scope

  CircularProgressIndicator(
    progress = currentProgress,
    modifier = modifier.width(width),
    color = color,
    trackColor = trackColor,
  )
}

suspend fun loadProgress(
  updateProgress: (Float) -> Unit,
  startingValue: Long,
  endingValue: Long
) {
  for (i in startingValue.toInt()..endingValue.toInt()) {
    updateProgress(i.toFloat() / endingValue)
    delay(endingValue)
  }
}
