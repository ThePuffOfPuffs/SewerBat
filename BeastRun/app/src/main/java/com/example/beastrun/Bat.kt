package com.example.beastrun

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

class Bat(val startingHeight: Dp) {
    var position by mutableStateOf(startingHeight)
    var size = 75.dp
    var xOffset = 0.dp

    private var velocity by mutableStateOf(0.dp)
    private val diveSpeed = (-8).dp
    private val reverseGravity = 0.5.dp
    private val bounce = (-5).dp

    var image by mutableIntStateOf(R.drawable.flyingbatgif)

    fun update() {
        velocity += reverseGravity
        position += velocity
        checkBounds()
    }

    fun dive() {
        velocity = diveSpeed
    }

    private fun checkBounds() {
        if (position < FLOOR.dp){
            position = FLOOR.dp
            velocity = 0.dp
        } else if (position > CEILING.dp) {
            position = CEILING.dp
            velocity = bounce
        }
    }

    fun death(){
        image = R.drawable.deathreapergif
        size = 100.dp
    }
    fun reset(){
        image = R.drawable.flyingbatgif
        size = 75.dp
        position = STARTING_BAT_HEIGHT.dp
        xOffset = -(PLAYING_AREA_WIDTH - 100).dp
    }

    @Composable
    fun BatImage(){
        AsyncImage(
            model = image,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(size)
        )
    }
}