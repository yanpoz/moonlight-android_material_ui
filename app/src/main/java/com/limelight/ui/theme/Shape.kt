package com.limelight.ui.theme

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.graphics.asComposePath
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.toPath

// TODO: replace with real Material expressive VerySunny shape
val VerySunnyShape = GenericShape { size, _ ->
    val polygon = RoundedPolygon.star(
        numVerticesPerRadius = 8,
        innerRadius = 0.5f,
        rounding = CornerRounding(size.width * 0.15f)
    )
    val path = polygon.toPath()
    val matrix = android.graphics.Matrix()
    val bounds = android.graphics.RectF()
    @Suppress("DEPRECATION")
    path.computeBounds(bounds, true)
    matrix.postTranslate(-bounds.left, -bounds.top)
    matrix.postScale(size.width / bounds.width(), size.height / bounds.height())
    path.transform(matrix)
    addPath(path.asComposePath())
}
