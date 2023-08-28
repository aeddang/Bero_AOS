package com.ironraft.pupping.bero.scene.component.graph

import android.graphics.PointF
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ironraft.pupping.bero.R
import com.skeleton.theme.AppTheme
import com.skeleton.theme.ColorApp
import com.skeleton.theme.ColorBrand
import com.skeleton.theme.ColorTransparent
import com.skeleton.theme.DimenIcon
import com.skeleton.theme.DimenMargin
import com.skeleton.theme.DimenStroke
import java.util.Locale
import java.util.UUID


data class GraphPolygonPoint(
    val id:String,
    val idx:Int,
    val pos:PointF
){
    @DrawableRes var icon:Int? = null
    var color:Color = ColorApp.black
    var bgColor:Color = ColorApp.white
    var isSelect:Boolean = false
    var isStroke:Boolean = false
    var isShadow:Boolean = false

    fun setup(
        selectIdx:List<Int>,
        selectedColor:Color,
        pointColor:Color,
        lineColor:Color,
        count:Int
    ): GraphPolygonPoint
    {
        if( idx == 0 ){
            bgColor = selectedColor
            color = ColorApp.white
            isSelect = true
            isStroke = true
            isShadow = true
        } else if( idx == count-1){
            color = selectedColor
            isSelect = true
            isStroke = true
            isShadow = true
            icon = R.drawable.route_flag
        } else {
            isSelect = selectIdx.find { it == idx } != null
            bgColor = if(isSelect) pointColor else lineColor
            color = ColorApp.white
            isStroke = isSelect
            isShadow = false

        }
        return this
    }
}

@Composable
fun PolygonGraph(
    modifier:Modifier = Modifier,
    screenHeight:Float = 100.0f,
    screenWidth:Float = 100.0f,
    selectIdx:List<Int> = listOf(),
    points:List<PointF> = listOf(),
    selectedColor:Color = ColorBrand.primary,
    pointColor:Color = ColorApp.green600,
    lineColor:Color = ColorBrand.primary,
    stroke:Float = DimenStroke.heavyUltra,
    usePoint:Boolean = true,
    action: ((Int) -> Unit)? = null
) {
    val dpi = LocalDensity.current.density
    fun getPaint():Paint{
        val paint = Paint()
        paint.color = lineColor
        paint.style = PaintingStyle.Stroke
        paint.strokeCap = StrokeCap.Round
        paint.strokeWidth = stroke * dpi
        paint.strokeJoin = StrokeJoin.Round
        return paint
    }
    val paint by remember { mutableStateOf( getPaint() ) }


    AppTheme {
       Box(
           modifier = modifier,
           contentAlignment = Alignment.TopStart
       ){

           Canvas(
               modifier = Modifier.fillMaxSize(),
               onDraw = {
                   val path = Path()
                   points.forEachIndexed { idx, value ->
                       val posx = size.width * value.x
                       val posy = size.height * value.y
                       if(idx  == 0) {
                           path.moveTo(posx, posy)
                       }
                       else path.lineTo(posx, posy)
                   }
                   drawIntoCanvas {canvas->
                       canvas.save()
                       canvas.drawPath(path, paint)
                       canvas.restore()
                   }
               }
           )
           if(usePoint){
               points.mapIndexed { idx, value ->
                   val pos = PointF(
                       screenWidth.toFloat() * value.x ,
                       screenHeight.toFloat() * value.y
                   )
                   GraphPolygonPoint(
                       id = UUID.randomUUID().toString(),
                       idx = idx,
                       pos = pos
                   ).setup(
                       selectIdx = selectIdx,
                       selectedColor = selectedColor,
                       pointColor = pointColor,
                       lineColor = lineColor,
                       count = points.count()
                   )
               }.filter { it.isSelect }.forEach{ p ->
                   Box(
                       modifier = Modifier
                           .offset( p.pos.x.dp, p.pos.y.dp)
                           .size(if (p.isShadow) DimenIcon.tiny.dp else DimenIcon.microUltra.dp)
                           .offset(
                               x = -(if (p.isShadow) DimenIcon.tiny/2 else DimenIcon.microUltra/2 ).dp,
                               y = -(if (p.isShadow) DimenIcon.tiny/2 else DimenIcon.microUltra/2 ).dp
                           )
                       ,
                       contentAlignment = Alignment.Center
                   ){
                       Box(
                           modifier = modifier
                               .shadow(
                                   elevation = DimenMargin.tiny.dp,
                                   shape = CircleShape,
                                   ambientColor = if (p.isShadow) DefaultShadowColor else ColorTransparent.clear,
                                   spotColor = if (p.isShadow) DefaultShadowColor else ColorTransparent.clear
                               )
                               .size(if (p.isShadow) DimenIcon.tiny.dp else DimenIcon.microUltra.dp)
                               .clip(CircleShape)
                               .background(p.bgColor)
                               .border(
                                   width = stroke.dp,
                                   color =
                                   if (p.isStroke) {
                                       if (p.isShadow) p.color else ColorTransparent.clear
                                   } else ColorTransparent.clear,
                                   shape = CircleShape
                               )

                       )
                       p.icon?.let {
                           Image(
                               painterResource(it),
                               contentDescription = "",
                               contentScale = ContentScale.Fit,
                               modifier = Modifier
                                   .size(DimenIcon.light.dp, DimenIcon.light.dp)
                                   .offset(x = 5.dp, y = (-8).dp),

                           )
                       }
                   }
               }
           }
       }
    }
}
@Preview
@Composable
fun GraphPolygonComposePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(ColorApp.white),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PolygonGraph(
            modifier = Modifier.size(100.dp, 100.dp),
            selectIdx = listOf(2,4),
            points = listOf(
                PointF(0.2f, 0.6f),
                PointF(0.5f, 0.5f),
                PointF(0.5f, 0.9f),
                PointF(0.2f, 0.5f),
                PointF(0.2f, 0.2f)
            )
        )
    }
}