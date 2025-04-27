package com.android.fire_and_rescue_departures.items

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.jvziyaoyao.scale.image.previewer.ImagePreviewer
import com.jvziyaoyao.scale.zoomable.pager.PagerGestureScope
import com.jvziyaoyao.scale.zoomable.previewer.PreviewerState
import com.jvziyaoyao.scale.zoomable.previewer.TransformLayerScope
import com.jvziyaoyao.scale.zoomable.previewer.rememberPreviewerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun FullScreenAsyncImage(
    imageUrl: String,
    previewerState: PreviewerState = rememberPreviewerState(pageCount = { 1 }),
    scope: CoroutineScope = rememberCoroutineScope(),
) {
    if (previewerState.visible) BackHandler {
        scope.launch {
            previewerState.close()
        }
    }

    ImagePreviewer(
        state = previewerState,
        detectGesture = PagerGestureScope(onTap = {
            scope.launch {
                previewerState.close()
            }
        }),
        previewerLayer = TransformLayerScope(
            background = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(0.8F))
                )
            }
        ),
        imageLoader = { index ->
            val painter = rememberAsyncImagePainter(model = imageUrl)
            Pair(painter, painter.intrinsicSize)
        },
        modifier = Modifier.zIndex(1f)
    )
}
