package com.example.beastrun

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class Pillar (val startingXposition: Dp) {
    var yPosition by mutableStateOf(0.dp)
    var xPosition by mutableStateOf(startingXposition)
    var bottomHeight by mutableStateOf(300.dp)
    var topHeight by mutableStateOf(100.dp)
    val width = 70.dp
    val gap = 200.dp
    private val leniancy = 10.dp
    var speed by mutableStateOf((-2).dp)

    fun update() {
        xPosition += speed
    }

    fun checkCollision(bat: Bat, top: Boolean) {
        if (xPosition > bat.xOffset - bat.size + leniancy && xPosition < bat.xOffset + bat.size - leniancy){
            if (top){
                if (bat.position > CEILING.dp - topHeight + leniancy){
                    playing = false
                    gameOver = true
                }
            } else {
                if (bat.position < bottomHeight - leniancy){
                    playing = false
                    gameOver = true
                }
            }
        }
    }
}