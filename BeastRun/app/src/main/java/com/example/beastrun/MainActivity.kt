package com.example.beastrun

import android.os.Bundle
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BeastRunTheme {
                var navController = rememberNavController ()
                NavHost(
                    navController = navController,
                    startDestination = ScreenA
                ) {
                    composable<ScreenA> {
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
    Image(
        painter = painterResource(id = R.drawable.lakehouse),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
    Row (
        modifier = Modifier
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = R.drawable.flyingbatgif,
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
    }
    Row( // Row for horizontal alignment
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        Text( // "Beast Run" title
            text = "Sewer Bat",
            fontFamily = blockyFontFamily,
            fontSize = 80.sp,
        )
    }

    Row( // Column for vertical alignment
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
    val startingHeight = 350
    val ceilingLimit = 0
    val floorLimit = 600
    val diveSpeed = 13
    val gravitySpeed = 0.75

    var batHeight by remember { mutableStateOf(startingHeight.dp) }
    var batVelocity by remember { mutableStateOf(0.dp) }
    var playing by remember { mutableStateOf(false) }
    var jumpAction by remember { mutableStateOf(false) }

    if (playing){
        LaunchedEffect(Unit) {
            while (true) {
                if (batHeight > ceilingLimit.dp) {
                    if (batHeight > floorLimit.dp && jumpAction) {
                        jumpAction = false
                        batVelocity = 0.dp
                    }
                    batVelocity -= gravitySpeed.dp // Apply gravity
                    batHeight += batVelocity
                }
                else if (jumpAction){
                    batHeight += batVelocity
                    jumpAction = false
                }
                else if (batHeight <= ceilingLimit.dp){
                    batHeight = ceilingLimit.dp
                }
                delay(16) //frame rate
            }
        }
    }
    //Screen Container
    Column (
        modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Playing area
        Row (
            modifier = Modifier
                .height(700.dp)
                .background(Color.Green)
        ) {
            //Bat gif container
            Row(
                modifier = Modifier.offset(y = batHeight)
            ){
                AsyncImage(
                    model = R.drawable.flyingbatgif,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(150.dp)
                )
            }
        }
        //Button box
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.Red),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            // "Score" button
            Button(
                onClick = {
                    navController.navigate(ScoreScreenData(name = "Jeff", score = 5))
                },
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text(
                    text = "SCORE",
                    fontFamily = blockyFontFamily,
                    fontSize = 20.sp
                )
            }
            // "Dive" button
            Button(
                onClick = {
                    if (!playing){
                        playing = true
                    }
                    if (batHeight < 650.dp) {
                        batVelocity = diveSpeed.dp
                        jumpAction = true
                    }
                },
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Text(
                    text = "DIVE",
                    fontFamily = blockyFontFamily,
                    fontSize = 60.sp
                )
            }
        }
    }
}

@Composable
fun ScoreScreen(args: ScoreScreenData){

}