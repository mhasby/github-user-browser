package com.pasteuri.githubuserbrowser.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun UserShimmer(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Box(modifier = Modifier.size(42.dp).clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = .5f)))
        Spacer(modifier = Modifier.width(16.dp))
        SingleShimmer(modifier = Modifier.height(18.dp).width(200.dp))
    }
}

@Composable
fun RepoShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        SingleShimmer(modifier = Modifier.height(21.dp).width(100.dp))
        Spacer(modifier = Modifier.height(8.dp))
        SingleShimmer(modifier = Modifier.height(18.dp).width(200.dp))
        Spacer(modifier = Modifier.height(12.dp))
        SingleShimmer(modifier = Modifier.height(14.dp).width(200.dp))
    }
}

@Composable
fun SingleShimmer(modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        color = MaterialTheme.colorScheme.secondary.copy(alpha = .5f),
        trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = .2f),
        gapSize = 0.dp,
        strokeCap = StrokeCap.Square,
        modifier = modifier.clip(RoundedCornerShape(4.dp))
    )
}