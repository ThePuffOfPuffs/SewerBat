package com.example.beastrun

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class Background (val image: Int){
    private val STARTING_XOFFSET = 200.0
    private val speed = (-0.5)

    var xOffset by mutableDoubleStateOf(STARTING_XOFFSET)
    @Composable
    fun StaticBackground(){
        //Background
        Image(
            painter = painterResource(image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }

    fun update(){
        xOffset += speed
    }

    @Composable
    fun PillarBackground(segmentHeight: Int, pillarHeight: Double){
        val noOfSegments = pillarHeight/segmentHeight
        Column (
            modifier = Modifier.fillMaxSize()
        ) {
            if (noOfSegments >= 1) {
                for (i in 1..noOfSegments.toInt()) {
                    Image(
                        painter = painterResource(image),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(segmentHeight.dp).fillMaxWidth()
                    )
                }
                val leftOver = pillarHeight - (noOfSegments - noOfSegments.toInt())*segmentHeight
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.height(leftOver.dp).fillMaxWidth()
                )
            } else {
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.height(pillarHeight.dp).fillMaxWidth()
                )
            }
        }
    }

    @Composable
    fun AnimateBackground(){
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.Center)
                    .wrapContentWidth(unbounded = true)
                    .fillMaxSize()
                    .offset(x = xOffset.dp)
            )
        }
    }
}