package com.example.foodrecipes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GenericDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    title: String,
    description: String? = null,
    positiveAction: PositiveAction? = null,
    negativeAction: NegativeAction? = null,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(text = title)},
        text = {
            if (description != null) {
                Text(text = description)
            }
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                if (negativeAction != null) {
                    Button(
                        onClick = { negativeAction.onNegativeAction },
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                    ) {
                        Text(text = negativeAction.negativeBtnText)
                    }
                }

                if (positiveAction != null) {
                    Button(
                        onClick = { positiveAction.onPositiveAction },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(text = positiveAction.positiveBtnText)
                    }
                }
            }
        }
    )
}

data class PositiveAction(
    val positiveBtnText: String,
    val onPositiveAction: () -> Unit
)

data class NegativeAction(
    val negativeBtnText: String,
    val onNegativeAction: () -> Unit
)

class GenericDialogInfo private constructor(builder: Builder) {

    val title: String
    val onDismiss: () -> Unit
    val description: String?
    val positiveAction: PositiveAction?
    val negativeAction: NegativeAction?

    init {
        if (builder.title == null) {
            throw NullPointerException("GenericDialogInfo title cannot be null.")
        }
        if (builder.onDismiss == null) {
            throw NullPointerException("GenericDialogInfo onDismiss  function cannot be null.")
        }
        this.title = builder.title!!
        this.onDismiss = builder.onDismiss!!
        this.description = builder.description
        this.positiveAction = builder.positiveAction
        this.negativeAction = builder.negativeAction
    }

    class Builder{

        var title: String? = null
            private set

        var onDismiss: (() -> Unit)? = null
            private set

        var description: String? = null
            private set

        var positiveAction: PositiveAction? = null
            private set

        var negativeAction: NegativeAction? = null
            private set

        fun title(title: String): Builder{
            this.title = title
            return this
        }

        fun onDismiss(onDismiss: () -> Unit): Builder{
            this.onDismiss = onDismiss
            return this
        }

        fun description(description: String): Builder{
            this.description = description
            return this
        }

        fun positiveAction(positiveAction: PositiveAction): Builder{
            this.positiveAction = positiveAction
            return this
        }

        fun negativeAction(negativeAction: NegativeAction): Builder{
            this.negativeAction = negativeAction
            return this
        }

        fun build() = GenericDialogInfo(this)
    }
}