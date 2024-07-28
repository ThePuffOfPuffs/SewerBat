package com.example.beastrun

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class Bat(val startingHeight: Dp) {
    var position by mutableStateOf(startingHeight)
    var size = 75.dp
    var xOffset = (-300).dp

    private var velocity by mutableStateOf(0.dp)
    private val diveSpeed = (-8).dp
    private val reverseGravity = 0.5.dp
    private val bounce = (-5).dp

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
}