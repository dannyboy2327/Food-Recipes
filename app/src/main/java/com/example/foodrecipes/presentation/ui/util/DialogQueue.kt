package com.example.foodrecipes.presentation.ui.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.foodrecipes.presentation.components.GenericDialogInfo
import com.example.foodrecipes.presentation.components.PositiveAction
import java.util.*

class DialogQueue {

    val queue: MutableState<Queue<GenericDialogInfo>> = mutableStateOf(LinkedList())

    fun removeHeadMessage() {
        if (queue.value.isNotEmpty()) {
            val update = queue.value
            update.remove() // remove first (oldest dialog)
            queue.value = ArrayDeque() // force recompose (bug?)
            queue.value = update
        }
    }

    fun appendErrorMessage(title: String, description: String) {
        queue.value.offer(
            GenericDialogInfo.Builder()
                .title(title)
                .onDismiss(this::removeHeadMessage)
                .description(description)
                .positiveAction(
                    PositiveAction(
                        positiveBtnText = "Ok",
                        onPositiveAction = this::removeHeadMessage,
                    )
                )
                .build()
        )
    }
}