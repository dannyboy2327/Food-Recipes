package com.example.foodrecipes.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun CircularIndeterminateProgressBar(
    isDisplayed: Boolean
) {
    if (isDisplayed) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (progressBar, text) = createRefs()

            CircularProgressIndicator(
                modifier = Modifier
                    .constrainAs(progressBar){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                },
                color = MaterialTheme.colors.primary
            )
            Text(
                text = "Loading...",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 15.sp,
                ),
                modifier = Modifier.constrainAs(text) {
                    top.linkTo(progressBar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

        }
    }
}