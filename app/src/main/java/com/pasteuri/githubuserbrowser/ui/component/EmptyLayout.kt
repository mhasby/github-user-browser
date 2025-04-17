package com.pasteuri.githubuserbrowser.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyLayout(
    title: String,
    description: String,
    action: String? = null,
    actionIcon: ImageVector? = null,
    actionColor: Color = ButtonDefaults.textButtonColors().contentColor,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .4f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            description,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = .3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        onActionClick?.let {
            TextButton(
                onClick = it,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = actionColor
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    actionIcon?.let { icon ->
                        Icon(
                            icon,
                            contentDescription = "Empty layout action",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(action.orEmpty())
                }
            }
        }
    }
}