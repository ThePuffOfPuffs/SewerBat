package com.example.beastrun

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import com.example.beastrun.ui.theme.BeastRunTheme
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

val blockyFontFamily = FontFamily(
    Font(R.font.blocky)
)
const val STARTING_PILLAR_XPOSITION = 60
const val NAVBAR_HEIGHT = 80
const val TOP_BAR_HEIGHT = 20
const val STARTING_BAT_HEIGHT = 350
const val FLOOR = -10
var PLAYING_AREA_HEIGHT = 0
var PLAYING_AREA_WIDTH = 0
var CEILING = 0
var score by mutableIntStateOf(0)
var playing by mutableStateOf(false)
var gameOver by mutableStateOf(false)
var generatedPillars by mutableStateOf(false)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeastRunTheme {
                PLAYING_AREA_HEIGHT = LocalConfiguration.current.screenHeightDp - NAVBAR_HEIGHT - TOP_BAR_HEIGHT
                PLAYING_AREA_WIDTH = LocalConfiguration.current.screenWidthDp
                CEILING = PLAYING_AREA_HEIGHT - 60
                val navController = rememberNavController ()
                Scaffold (
                    topBar = {},
                    bottomBar = {}
                ) {
                    contentPadding ->
                    NavHost(
                        navController = navController,
                        modifier = Modifier.padding(contentPadding),
                        startDestination = ScreenA
                    ) {
                        composable<ScreenA> {
                            Background(R.drawable.sewercropped).StaticBackground()
                            StartScreen(navController)
                        }
                        composable<PlayScreenData> {
                            PlayScreen(navController)
                        }
                        composable<ScoreScreenData> {
                            val args = it.toRoute<ScoreScreenData>()
                            ScoreScreen(args)
                        }
                    }
                }
            }
        }
    }
}

@Serializable
object ScreenA

@Serializable
object PlayScreenData

@Serializable
data class ScoreScreenData(
    val name: String?,
    val score: Int
)

@Composable
fun StartScreen(navController: NavController){
    Row (
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = R.drawable.flyingbatgif,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(75.dp)
        )
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        Text( // "SEWER BAT" title
            text = "Sewer Bat",
            fontFamily = blockyFontFamily,
            fontSize = 80.sp,
        )
    }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        Button( // "START" button
            onClick = {
                navController.navigate(PlayScreenData)
            }
        ) {
            Text(
                text = "START",
                fontFamily = blockyFontFamily,
                fontSize = 60.sp
            )
        }
    }
}

@Composable
fun PlayScreen(navController: NavController){
    val background = remember { Background(R.drawable.sewer) }
    val bat = remember { Bat(startingHeight = STARTING_BAT_HEIGHT.dp) }
    val pillar = remember { Pillar(startingXposition = STARTING_PILLAR_XPOSITION.dp) }

    bat.xOffset = -(PLAYING_AREA_WIDTH - 100).dp
    background.AnimateBackground()
    //Screen Container
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Top bar
        Row (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Background(R.drawable.brownpipe).StaticBackground()
        }

        PlayAreaContainer(bat, pillar)
        NavBar(navController = navController, bat, pillar, background)
    }

    FrameLoop(bat, pillar, background)
}

@Composable
fun PlayAreaContainer(bat: Bat, pillar: Pillar){
    //Playing area
    Box (
        modifier = Modifier
            .height(PLAYING_AREA_HEIGHT.dp)
            .width(PLAYING_AREA_WIDTH.dp)
    ){
        if (!generatedPillars) {
            pillar.randomBottomHeight()
            generatedPillars = true
        }

        //Top pipe
        Box (
            modifier = Modifier
                .height(pillar.topHeight)
                .width(pillar.width)
                .align(Alignment.TopEnd)
                .offset(x = pillar.xPosition)
                .background(Color.Cyan)
        ) {

            Background(R.drawable.pillar).PillarBackground(pillar.SEGMENT_HEIGHT.value.toInt(), pillar.topHeight.value.toDouble())
            pillar.checkCollision(bat, true)
        }

        //Bottom pipe
        Box (
            modifier = Modifier
                .height(pillar.bottomHeight)
                .width(pillar.width)
                .align(Alignment.BottomEnd)
                .offset(x = pillar.xPosition)
                .background(Color.Cyan)
        ) {
            Background(R.drawable.pillar).PillarBackground(pillar.SEGMENT_HEIGHT.value.toInt(), pillar.bottomHeight.value.toDouble())
            pillar.checkCollision(bat, false)
        }

        //Bat gif container
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = bat.xOffset, y = -bat.position)
        ){
            bat.BatImage()
        }

        // "Score" text
        Button(
            onClick = {
            }
        ) {
            Text(
                text = "SCORE: $score",
                fontFamily = blockyFontFamily,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
fun NavBar(navController: NavController, bat: Bat, pillar: Pillar, background: Background){
    Box(modifier = Modifier.height(NAVBAR_HEIGHT.dp)){
        Background(R.drawable.brownpipe).StaticBackground()
        //Button box
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            //1st column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                // "Score" button
                Button(
                    onClick = {
                        ResetGame(bat, pillar, background)
                        navController.navigate(ScoreScreenData(name = "Jeff", score = 5))
                    }
                ) {
                    Text(
                        text = "SCORE",
                        fontFamily = blockyFontFamily,
                        fontSize = 20.sp
                    )
                }
            }
            //2nd column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                // "Dive" button
                Button(
                    onClick = {
                        if (!gameOver){
                            playing = true
                            bat.dive()
                        }
                    }
                ) {
                    Text(
                        text = "DIVE",
                        fontFamily = blockyFontFamily,
                        fontSize = 40.sp
                    )
                }
            }
            //3rd column
            Column(
                modifier = Modifier.weight(1f)
            ){

            }
        }
    }
}

@Composable
fun GameOverPopUp(bat: Bat, pillar: Pillar, background: Background){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "YOU LOSE",
            fontFamily = blockyFontFamily,
            fontSize = 80.sp,
            color = Color.Red
        )
        // "Play again" button
        Button(
            onClick = {
                ResetGame(bat, pillar, background)
            }
        ) {
            Text(
                text = "PLAY AGAIN",
                fontFamily = blockyFontFamily,
                fontSize = 30.sp
            )
        }
    }
}

@Composable
fun FrameLoop(bat: Bat, pillar: Pillar, background: Background){
    LaunchedEffect(Unit) {
        while (true) {
            if (playing){
                background.update()
                bat.update()
                pillar.update()
            }
            delay(16)
        }
    }
    if (gameOver){
        bat.death()
        GameOverPopUp(bat, pillar, background)
    }
}
fun ResetGame(bat: Bat, pillar: Pillar, background: Background){
    score = 0
    playing = false
    gameOver = false
    bat.reset()
    background.reset()
    pillar.pillarReset(true)
}
@Composable
fun ScoreScreen(args: ScoreScreenData){

}