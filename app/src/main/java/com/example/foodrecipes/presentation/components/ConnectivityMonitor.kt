package com.example.foodrecipes.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.unit.dp
import java.lang.reflect.Modifier


@Composable
fun ConnectivityMonitor(
    isNetworkAvailable: Boolean,
) {
    if (!isNetworkAvailable) {
        Column(
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "No Network Connection",
                modifier = androidx.compose.ui.Modifier
                    .align(CenterHorizontally)
                    .padding(8.dp),
                style = MaterialTheme.typography.h6
            )
        }
    }
}