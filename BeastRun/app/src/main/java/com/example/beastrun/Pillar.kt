package com.example.beastrun

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class Pillar (val startingXposition: Dp) {
    var xPosition by mutableStateOf(startingXposition)
    var bottomHeight by mutableStateOf(100.dp)
    var topHeight by mutableStateOf(100.dp)
    val width = 70.dp

    private var scoreCount by mutableStateOf(false)
    private var speed by mutableStateOf((-2).dp)
    private val gap = 200.dp
    private val leniency = 10.dp
    private val smallestHeight = 50
    private val tallestHeight = 400

    fun update() {
        xPosition += speed
        pillarReset(false)
    }

    fun randomBottomHeight() {
        bottomHeight = Random.nextInt(smallestHeight, tallestHeight + 1).dp
        calcTopHeight()
    }

    private fun calcTopHeight() {
        topHeight = CEILING.dp - (bottomHeight + gap)
    }

    fun checkCollision(bat: Bat, top: Boolean) {
        if (xPosition > bat.xOffset - bat.size + leniency && xPosition < bat.xOffset + bat.size - leniency){
            if (top){
                if (bat.position > CEILING.dp - topHeight + leniency){
                    playing = false
                    gameOver = true
                }
            } else if (bat.position < bottomHeight - leniency){
                playing = false
                gameOver = true
            }
        } else if (!scoreCount && bat.xOffset > xPosition){
            score++
            scoreCount = true
        }
    }

    fun pillarReset(override: Boolean){
        if (override){
            scoreCount = false
            xPosition = startingXposition
        } else if (xPosition <= -PLAYING_AREA_WIDTH.dp){
            generatedPillars = false
            scoreCount = false
            xPosition = startingXposition
        }
    }
}