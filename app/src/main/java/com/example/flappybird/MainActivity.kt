package com.example.flappybird

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF87CEEB) // רקע כחול בהיר
                ) {
                    FlappyBirdGame()
                }
            }
        }
    }
}

enum class GameState { START, PLAYING, GAME_OVER }

@Composable
fun FlappyBirdGame() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()
        val density = LocalDensity.current
        val coroutineScope = rememberCoroutineScope()

        var gameState by remember { mutableStateOf(GameState.START) }
        var birdY by remember { mutableStateOf(screenHeight / 2) }
        var birdVelocity by remember { mutableStateOf(0f) }
        var pipes by remember { mutableStateOf(listOf(Pipe(screenWidth, screenHeight))) }
        var score by remember { mutableStateOf(0) }
        var lives by remember { mutableStateOf(3) }
        var invincible by remember { mutableStateOf(false) }
        var birdFrame by remember { mutableStateOf(0) }
        var highScore by remember { mutableStateOf(0) } // שיא נקודות

        val birdImages = listOf(
            painterResource(id = R.drawable.flappy_bird_fly),
            painterResource(id = R.drawable.flappy_bird)
        )
        val gravity = 0.5f
        val jumpStrength = -12f
        val pipeSpeed = 6f

        // אנימציית ציפור
        LaunchedEffect(gameState) {
            while (gameState == GameState.PLAYING) {
                birdFrame = (birdFrame + 1) % 2
                delay(200L)
            }
        }

        // מחזור המשחק
        LaunchedEffect(gameState) {
            while (gameState == GameState.PLAYING) {
                birdVelocity += gravity
                birdY += birdVelocity

                var hit = false
                var collidedPipe: Pipe? = null

                val birdWidth = screenHeight * 0.05f
                val birdHeight = birdWidth
                val birdX = screenWidth * 0.1f
                val birdYPos = birdY

                // בדיקת התנגשות עם צינורות
                pipes.forEach { pipe ->
                    if (birdX < pipe.x + pipe.width &&
                        birdX + birdWidth > pipe.x &&
                        (birdYPos < pipe.top || birdYPos + birdHeight > pipe.bottom)
                    ) {
                        hit = true
                        collidedPipe = pipe
                    }

                    if (!pipe.passed && pipe.x + pipe.width < birdX) {
                        pipe.passed = true
                        score += 1
                        if (score > highScore) {
                            highScore = score // עדכון שיא בזמן אמת
                        }
                    }
                }

                // בדיקת חוץ מסך
                if (birdY < 0f) hit = true
                if (birdY + birdHeight > screenHeight) hit = true

                if (hit && !invincible) {
                    lives -= 1
                    invincible = true

                    // אם פגיעה בצינור → חזרה למרכז הפתח
                    if (collidedPipe != null) {
                        birdY = (collidedPipe.top + collidedPipe.bottom) / 2 - birdHeight / 2
                    } else {
                        birdY = screenHeight / 2
                    }

                    birdVelocity = 0f

                    coroutineScope.launch {
                        delay(1000L)
                        invincible = false
                    }

                    if (lives <= 0) {
                        gameState = GameState.GAME_OVER
                        if (score > highScore) {
                            highScore = score
                        }
                    }
                }

                // הזזת צינורות
                pipes = pipes.map { it.copy(x = it.x - pipeSpeed) }.filter { it.x + it.width > 0f }

                // יצירת צינורות חדשים
                if (pipes.isEmpty() || pipes.last().x < screenWidth / 2) {
                    val lastTop = pipes.lastOrNull()?.top
                    pipes = pipes + Pipe(screenWidth, screenHeight, lastTop)
                }

                delay(16L)
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(gameState) {
                    detectTapGestures {
                        when (gameState) {
                            GameState.START -> {
                                gameState = GameState.PLAYING
                                birdY = screenHeight / 2
                                birdVelocity = 0f
                                pipes = listOf(Pipe(screenWidth, screenHeight))
                                score = 0
                                lives = 3
                            }
                            GameState.PLAYING -> birdVelocity = jumpStrength
                            GameState.GAME_OVER -> gameState = GameState.START
                        }
                    }
                }
        ) {
            // רקע
            Image(
                painter = painterResource(id = R.drawable.flappy_bird_bg),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            when (gameState) {
                GameState.START -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color(0x88000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "FLAPPY BIRD",
                                color = Color.Yellow,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.shadow(4.dp)
                            )
                            Spacer(Modifier.height(20.dp))

                            val birdSizeDp = with(density) { (screenHeight * 0.08f).toDp() }
                            var startBirdFrame by remember { mutableStateOf(0) }
                            LaunchedEffect(Unit) {
                                while (gameState == GameState.START) {
                                    startBirdFrame = (startBirdFrame + 1) % birdImages.size
                                    delay(300L)
                                }
                            }

                            Image(
                                painter = birdImages[startBirdFrame],
                                contentDescription = "Start Bird",
                                modifier = Modifier.size(birdSizeDp)
                            )

                            Spacer(Modifier.height(20.dp))
                            Box(
                                Modifier
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .background(Color.Green, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 40.dp, vertical = 16.dp)
                            ) {
                                Text(
                                    "START",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                GameState.PLAYING -> {
                    // ציפור
                    val birdSizeDp = with(density) { (screenHeight * 0.05f).toDp() }
                    val birdXOffsetDp = with(density) { (screenWidth * 0.1f).toDp() }
                    val birdYOffsetDp = with(density) { birdY.toDp() }

                    Image(
                        painter = birdImages[birdFrame],
                        contentDescription = "Bird",
                        modifier = Modifier
                            .size(birdSizeDp)
                            .offset(x = birdXOffsetDp, y = birdYOffsetDp)
                    )

                    // צינורות עם כובעים
                    pipes.forEach { pipe ->
                        // צינור עליון
                        Canvas(
                            modifier = Modifier
                                .width(with(density) { pipe.width.toDp() })
                                .height(with(density) { pipe.top.toDp() })
                                .offset(x = with(density) { pipe.x.toDp() }, y = 0.dp)
                        ) {
                            val capHeight = size.width * 0.25f
                            val capWidth = size.width * 1.2f

                            drawRect(Color(0xFF228B22), Offset(0f, 0f), Size(size.width, size.height - capHeight))
                            drawRect(Color(0xFF228B22), Offset(-(capWidth - size.width)/2, size.height - capHeight), Size(capWidth, capHeight))
                            drawRect(Color.Black, Offset(0f, 0f), Size(size.width, size.height - capHeight), style = Stroke(4f))
                            drawRect(Color.Black, Offset(-(capWidth - size.width)/2, size.height - capHeight), Size(capWidth, capHeight), style = Stroke(4f))
                        }

                        // צינור תחתון
                        Canvas(
                            modifier = Modifier
                                .width(with(density) { pipe.width.toDp() })
                                .height(with(density) { (screenHeight - pipe.bottom).toDp() })
                                .offset(x = with(density) { pipe.x.toDp() }, y = with(density) { pipe.bottom.toDp() })
                        ) {
                            val capHeight = size.width * 0.25f
                            val capWidth = size.width * 1.2f

                            drawRect(Color(0xFF228B22), Offset(-(capWidth - size.width)/2, 0f), Size(capWidth, capHeight))
                            drawRect(Color(0xFF228B22), Offset(0f, capHeight), Size(size.width, size.height - capHeight))
                            drawRect(Color.Black, Offset(-(capWidth - size.width)/2, 0f), Size(capWidth, capHeight), style = Stroke(4f))
                            drawRect(Color.Black, Offset(0f, capHeight), Size(size.width, size.height - capHeight), style = Stroke(4f))
                        }
                    }

                    // ניקוד
                    Text(
                        text = "Score: $score",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopCenter)
                    )

                    // שיא נקודות
                    Text(
                        text = "High Score: $highScore",
                        color = Color.Yellow,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd)
                    )

                    // חיים
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(lives) {
                            Text("❤️", fontSize = MaterialTheme.typography.headlineMedium.fontSize)
                        }
                    }
                }

                GameState.GAME_OVER -> {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(Color(0x88000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "GAME OVER",
                                color = Color.Red,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(Modifier.height(16.dp))
                            Box(
                                Modifier
                                    .shadow(8.dp, RoundedCornerShape(16.dp))
                                    .background(Color.Green, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 40.dp, vertical = 16.dp)
                            ) {
                                Text(
                                    "RESTART",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Pipe(
    var x: Float,
    val screenHeight: Float,
    val width: Float = 50f,
    val top: Float = Random.nextInt(100, 400).toFloat(),
    val gap: Float = 150f,
    var passed: Boolean = false
) {
    val bottom: Float
        get() = top + gap

    constructor(screenWidth: Float, screenHeight: Float, lastTop: Float? = null) : this(
        x = screenWidth,
        screenHeight = screenHeight,
        width = screenWidth * 0.1f,
        top = lastTop?.let {
            val maxShift = screenHeight * 0.15f
            (it + Random.nextFloat() * maxShift * 2 - maxShift)
                .coerceIn(screenHeight * 0.1f, screenHeight * 0.6f)
        } ?: Random.nextInt((screenHeight * 0.1).toInt(), (screenHeight * 0.6).toInt()).toFloat(),
        gap = maxOf(screenHeight * 0.05f * 1.2f, screenHeight * 0.15f),
        passed = false
    )
}
