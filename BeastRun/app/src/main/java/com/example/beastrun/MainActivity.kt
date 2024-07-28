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
const val NAVBAR_HEIGHT = 90
const val STARTING_BAT_HEIGHT = 350
const val FLOOR = -10
const val PLAYING_AREA_HEIGHT = 700
const val PLAYING_AREA_WIDTH = 400
const val CEILING = PLAYING_AREA_HEIGHT - 60
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
                            Background(R.drawable.lakehouse).StaticBackground()
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
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(100.dp)
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
        NavBar(navController = navController, bat)
    }

    FrameLoop(bat, pillar, background)
}

@Composable
fun FrameLoop(bat: Bat, pillar: Pillar, background: Background){
    if (playing) {
        LaunchedEffect(Unit) {
            while (true) {
                background.update()
                bat.update()
                pillar.update()
                delay(16)
            }
        }
    } else {
        if (gameOver){
            GameOverPopUp(pillar)
        }else{
            bat.position = STARTING_BAT_HEIGHT.dp
        }
    }
}

@Composable
fun PlayAreaContainer(bat: Bat, pillar: Pillar){
    //Playing area
    Box (
        modifier = Modifier
            .height(PLAYING_AREA_HEIGHT.dp)
            .width(PLAYING_AREA_WIDTH.dp)
    ){
        //Bat gif container
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = bat.xOffset, y = -bat.position)
        ){
            AsyncImage(
                model = R.drawable.flyingbatgif,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(bat.size)
            )
        }
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
            Background(R.drawable.pillar).StaticBackground()
            pillar.checkCollision(bat, true)
        }

        //Bottom pipe
        Box (
            modifier = Modifier
                .height(pillar.bottomHeight)
                .width(pillar.width)
                .align(Alignment.BottomEnd)
                .offset(x = pillar.xPosition)
        ) {
            Background(R.drawable.pillar).StaticBackground()
            pillar.checkCollision(bat, false)
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
fun NavBar(navController: NavController, bat: Bat){
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
                        playing = false
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
                        fontSize = 50.sp
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
fun GameOverPopUp(pillar: Pillar){
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
                score = 0
                gameOver = false
                pillar.pillarReset(true)
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
fun ScoreScreen(args: ScoreScreenData){

}