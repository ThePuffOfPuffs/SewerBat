package com.example.beastrun

import android.app.Activity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Animatable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
const val STARTING_PILLAR = 0
const val NAVBAR_HEIGHT = 90
const val STARTING_HEIGHT = 350
const val FLOOR = -10
const val PLAYING_AREA = 700
const val CEILING = PLAYING_AREA - 60
var playing by mutableStateOf(false)
var gameOver by mutableStateOf(false)

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
                            GenerateBackground(1)
                            StartScreen(navController)
                        }
                        composable<PlayScreenData> {
                            GenerateBackground(2)
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
fun GenerateBackground(backgroundNo: Int){
    val backgroundImage = when (backgroundNo) {
        0 -> R.drawable.brownpipe
        1 -> R.drawable.lakehouse
        2 -> R.drawable.sewer
        // Default image
        else -> R.drawable.brownpipe
    }
    //Background
    Image(
        painter = painterResource(id = backgroundImage),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxHeight()
    )
}

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
    val bat = remember { Bat(startingHeight = STARTING_HEIGHT.dp) }
    val pillar = remember { Pillar(startingXposition = STARTING_PILLAR.dp) }

    if (playing) {
        LaunchedEffect(Unit) {
            while (true) {
                bat.update()
                pillar.update()
                delay(16)
            }
        }
    } else {
        bat.position = STARTING_HEIGHT.dp

        if (gameOver){
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "YOU LOSE",
                    fontFamily = blockyFontFamily,
                    fontSize = 80.sp,
                    color = Color.Red
                )
            }
        }
    }
    //Screen Container
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Top pipe
        Row (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            GenerateBackground(backgroundNo = 0)
        }
        //Playing area
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(PLAYING_AREA.dp)
        ) {
            Box (
                modifier = Modifier.fillMaxSize()
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
                //Top pipe
                Box (
                    modifier = Modifier
                        .height(pillar.topHeight)
                        .width(pillar.width)
                        .align(Alignment.TopEnd)
                        .offset(x = pillar.xPosition)
                        .background(Color.Cyan)
                ) {
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
                    pillar.checkCollision(bat, false)
                }
            }
        }
        NavBar(navController = navController, bat)
    }
}

@Composable
fun NavBar(navController: NavController, bat: Bat){
    Box(modifier = Modifier.height(NAVBAR_HEIGHT.dp)){
        GenerateBackground(backgroundNo = 0)
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
                        playing = true
                        bat.dive()
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
fun ScoreScreen(args: ScoreScreenData){

}