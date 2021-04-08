package com.example.foodrecipes.presentation.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet

@Composable
fun CircularIndeterminateProgressBar(
    isDisplayed: Boolean,
    verticalBias: Float
) {
    if (isDisplayed) {
        ConstraintLayout(
                modifier = Modifier.fillMaxSize(),
        ) {
            val progressBar = createRef()
            val topBias = createGuidelineFromTop(verticalBias)

            CircularProgressIndicator(
                color = MaterialTheme.colors.primary,
                modifier = Modifier.constrainAs(progressBar) {
                    top.linkTo(topBias)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }
    }
}