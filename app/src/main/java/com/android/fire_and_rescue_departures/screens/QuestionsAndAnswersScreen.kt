package com.android.fire_and_rescue_departures.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.android.fire_and_rescue_departures.consts.faqItems
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.Lucide
import androidx.compose.material3.HorizontalDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsAndAnswersScreen() {

    LazyColumn {
        items(count = faqItems.size) { index ->
            val item = faqItems[index]

            var expanded by remember { mutableStateOf(false) }
            val degrees by animateFloatAsState(if (expanded) -90f else 90f)

            Column {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { expanded = expanded.not() }
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    BasicText(item.title)
                    Image(
                        Lucide.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier.rotate(degrees),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                }
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(
                        spring(
                            stiffness = Spring.StiffnessMediumLow,
                            visibilityThreshold = IntSize.VisibilityThreshold
                        )
                    ),
                    exit = shrinkVertically()
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        BasicText(item.text)
                    }
                }

                if (index != faqItems.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}
