package com.limelight.ui.components

import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.limelight.grid.assets.CachedAppAssetLoader
import com.limelight.nvstream.http.NvApp

@Composable
fun AppImage(
    app: NvApp,
    assetLoader: CachedAppAssetLoader,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->
            assetLoader.populateImageView(app, imageView, null)
        }
    )
}
