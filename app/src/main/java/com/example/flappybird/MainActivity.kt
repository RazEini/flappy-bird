package com.example.flappybird

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
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
                    color = Color(0xFF87CEEB)
                ) {
                    FlappyBirdGame()
                }
            }
        }
    }
}

enum class GameState { START, PLAYING, GAME_OVER }

@SuppressLint("UnusedBoxWithConstraintsScope")
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
        var highScore by remember { mutableStateOf(0) }

        val birdImages = listOf(
            painterResource(id = R.drawable.flappy_bird_fly),
            painterResource(id = R.drawable.flappy_bird)
        )
        val gravity = 0.5f
        val jumpStrength = -12f
        val pipeSpeed = 6f

        // אנימציית ציפור בזמן המשחק
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

                val birdWidth = screenHeight * 0.05f
                val birdRadius = birdWidth / 2
                val birdX = screenWidth * 0.1f
                val birdCenterX = birdX + birdRadius
                val birdCenterY = birdY + birdRadius

                pipes.forEach { pipe ->
                    val pipeLeft = pipe.x
                    val pipeRight = pipe.x + pipe.width
                    val pipeTop = pipe.top
                    val pipeBottom = pipe.bottom

                    val collisionTop = birdCenterX + birdRadius > pipeLeft &&
                            birdCenterX - birdRadius < pipeRight &&
                            birdCenterY - birdRadius < pipeTop

                    val collisionBottom = birdCenterX + birdRadius > pipeLeft &&
                            birdCenterX - birdRadius < pipeRight &&
                            birdCenterY + birdRadius > pipeBottom

                    if ((collisionTop || collisionBottom) && !invincible) {
                        lives -= 1
                        invincible = true
                        birdY = (pipe.top + pipe.bottom) / 2 - birdRadius
                        birdVelocity = 0f

                        coroutineScope.launch {
                            delay(1000L)
                            invincible = false
                        }

                        if (lives <= 0) {
                            gameState = GameState.GAME_OVER
                            if (score > highScore) highScore = score
                        }
                    }

                    if (!pipe.passed && pipe.x + pipe.width < birdX) {
                        pipe.passed = true
                        score += 1
                        if (score > highScore) highScore = score
                    }
                }

                if (birdCenterY - birdRadius < 0f && !invincible) {
                    lives -= 1
                    invincible = true
                    birdY = birdRadius
                    birdVelocity = 0f
                    coroutineScope.launch {
                        delay(1000L)
                        invincible = false
                    }
                    if (lives <= 0) gameState = GameState.GAME_OVER
                }
                if (birdCenterY + birdRadius > screenHeight && !invincible) {
                    lives -= 1
                    invincible = true
                    birdY = screenHeight - birdWidth
                    birdVelocity = 0f
                    coroutineScope.launch {
                        delay(1000L)
                        invincible = false
                    }
                    if (lives <= 0) gameState = GameState.GAME_OVER
                }

                pipes = pipes.map { it.copy(x = it.x - pipeSpeed) }.filter { it.x + it.width > 0f }

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
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center // כל התוכן במרכז
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // כותרת עם קופסה בולטת
                            Box(
                                Modifier
                                    .background(Color(0x88000000), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                                    .shadow(6.dp, RoundedCornerShape(16.dp))
                            ) {
                                Text(
                                    "FLAPPY BIRD",
                                    color = Color.Yellow,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(Modifier.height(30.dp)) // רווח בין כותרת לציפור

                            val birdSizeDp = with(density) { (screenHeight * 0.08f).toDp() }
                            var startBirdFrame by remember { mutableStateOf(0) }

                            // אנימציית ציפור floating למעלה-למטה
                            val infiniteTransition = rememberInfiniteTransition()
                            val floatOffset by infiniteTransition.animateFloat(
                                initialValue = -10f,
                                targetValue = 10f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(800, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )

                            LaunchedEffect(Unit) {
                                while (gameState == GameState.START) {
                                    startBirdFrame = (startBirdFrame + 1) % birdImages.size
                                    delay(300L)
                                }
                            }

                            Image(
                                painter = birdImages[startBirdFrame],
                                contentDescription = "Start Bird",
                                modifier = Modifier
                                    .size(birdSizeDp)
                                    .offset(y = with(density) { floatOffset.toDp() })
                            )

                            Spacer(Modifier.height(30.dp)) // רווח בין ציפור לכפתור START

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

                    // צינורות
                    pipes.forEach { pipe ->
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
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopCenter)
                            .shadow(6.dp, RoundedCornerShape(12.dp))
                            .background(Color(0xAA000000), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Score: $score",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // שיא
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd)
                            .shadow(6.dp, RoundedCornerShape(12.dp))
                            .background(Color(0xAAFFD700), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "High: $highScore",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // חיים עם אנימציה + קופסה אדומה
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart)
                            .background(Color(0xAAFF0000), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(3) { index ->
                            val scale by animateFloatAsState(targetValue = if (index < lives) 1f else 0f)
                            val alpha by animateFloatAsState(targetValue = if (index < lives) 1f else 0f)
                            if (scale > 0f) {
                                Text(
                                    "❤️",
                                    fontSize = 20.sp,
                                    modifier = Modifier.scale(scale).alpha(alpha)
                                )
                            }
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
