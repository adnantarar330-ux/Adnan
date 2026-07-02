package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MediaContent
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
    media: MediaContent,
    onClose: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var currentSeconds by remember { mutableLongStateOf(42) }
    val totalSeconds = 7200L // 2 Hours

    var selectedResolution by remember { mutableStateOf("1080p") }
    var selectedSpeed by remember { mutableFloatStateOf(1.0f) }
    var isSubtitlesOn by remember { mutableStateOf(true) }
    var selectedAudioTrack by remember { mutableStateOf("English (5.1 Atmos)") }

    var volumeLevel by remember { mutableFloatStateOf(0.7f) }
    var brightnessLevel by remember { mutableFloatStateOf(0.8f) }

    var showControls by remember { mutableStateOf(true) }
    var showQualityMenu by remember { mutableStateOf(false) }
    var showSpeedMenu by remember { mutableStateOf(false) }
    var showAudioMenu by remember { mutableStateOf(false) }

    // Auto update progress bar if playing
    LaunchedEffect(key1 = isPlaying) {
        if (isPlaying) {
            while (currentSeconds < totalSeconds) {
                delay(1000)
                currentSeconds += 1
            }
        }
    }

    // Auto hide controls after 5 seconds
    LaunchedEffect(key1 = showControls) {
        if (showControls) {
            delay(5000)
            showControls = false
        }
    }

    // Capture left/right gestures to change brightness and volume
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val x = change.position.x
                    val isLeftHalf = x < size.width / 2
                    if (isLeftHalf) {
                        // Gesture brightness
                        brightnessLevel = (brightnessLevel - (dragAmount.y / 800f)).coerceIn(0f, 1f)
                    } else {
                        // Gesture volume
                        volumeLevel = (volumeLevel - (dragAmount.y / 800f)).coerceIn(0f, 1f)
                    }
                    showControls = true
                }
            }
            .clickable { showControls = !showControls }
    ) {
        // SIMULATED MOVIE STREAM VIEW (Visual ambiance matching the title)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            SecondaryAccent.copy(alpha = 0.35f * brightnessLevel),
                            Color.Black
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = "Simulated Stream",
                    tint = PrimaryAccent.copy(alpha = 0.6f),
                    modifier = Modifier.size(96.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Streaming CineStream Server... (${selectedResolution})",
                    color = GraySecondary,
                    fontSize = 14.sp
                )
            }
        }

        // SUBTITLE OVERLAY (SIMULATED DUAL SUBTITLES)
        if (isSubtitlesOn && currentSeconds in 10..120) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Cooper: We're pioneers, we have to look up.",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "[Somos pioneros, tenemos que mirar hacia arriba]",
                        color = PrimaryAccent,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // GESTURE METERS (Show floating when dragging)
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brightness indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.BrightnessMedium, contentDescription = "Brightness", tint = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { brightnessLevel },
                    color = PrimaryAccent,
                    trackColor = Color.DarkGray,
                    modifier = Modifier.width(60.dp).clip(CircleShape)
                )
            }

            // Volume indicator
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.VolumeUp, contentDescription = "Volume", tint = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { volumeLevel },
                    color = RosePremium,
                    trackColor = Color.DarkGray,
                    modifier = Modifier.width(60.dp).clip(CircleShape)
                )
            }
        }

        // SKIP INTRO BUTTON (Show dynamically at start of video)
        if (currentSeconds in 10..60) {
            Button(
                onClick = { currentSeconds = 240 }, // Skip 4 mins
                colors = ButtonDefaults.buttonColors(containerColor = GlassyOverlay),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 120.dp, end = 24.dp)
            ) {
                Text("SKIP INTRO ⏭", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // CONTROLS INTERFACE (Fade Animation)
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f))
            ) {
                // TOP HEADER CONTROLS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onClose, modifier = Modifier.testTag("player_close_button")) {
                            Icon(Icons.Default.Close, contentDescription = "Close Player", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(media.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("Now Streaming • ${media.quality}", color = PrimaryAccent, fontSize = 12.sp)
                        }
                    }

                    // Top Action Badges (Chromecast, Speed, Subtitles, Audio)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { /* Chromecast toggle */ }) {
                            Icon(Icons.Default.Cast, contentDescription = "Chromecast", tint = Color.White)
                        }
                        IconButton(onClick = { isSubtitlesOn = !isSubtitlesOn }) {
                            Icon(
                                imageVector = if (isSubtitlesOn) Icons.Filled.Subtitles else Icons.Outlined.Subtitles,
                                contentDescription = "Subtitles",
                                tint = if (isSubtitlesOn) PrimaryAccent else Color.White
                            )
                        }
                        IconButton(onClick = { showAudioMenu = true }) {
                            Icon(Icons.Default.Audiotrack, contentDescription = "Audio Tracks", tint = Color.White)
                        }
                    }
                }

                // CENTER PLAYBACK CONTROLS
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rewind 10s
                    IconButton(onClick = { currentSeconds = (currentSeconds - 10).coerceAtLeast(0) }) {
                        Icon(Icons.Default.Replay10, contentDescription = "Rewind", tint = Color.White, modifier = Modifier.size(36.dp))
                    }

                    // Main Play/Pause
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(RosePremium, CircleShape)
                            .clickable { isPlaying = !isPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "PlayPause",
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    // FastForward 10s
                    IconButton(onClick = { currentSeconds = (currentSeconds + 10).coerceAtMost(totalSeconds) }) {
                        Icon(Icons.Default.Forward10, contentDescription = "Forward", tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                }

                // BOTTOM CONTROLS PANEL
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Time and Progress text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(currentSeconds),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "-${formatTime(totalSeconds - currentSeconds)}",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Seek Bar Slider
                    Slider(
                        value = currentSeconds.toFloat(),
                        onValueChange = { currentSeconds = it.toLong() },
                        valueRange = 0f..totalSeconds.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = RosePremium,
                            activeTrackColor = RosePremium,
                            inactiveTrackColor = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Controls Row (Quality, Speed, PiP)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Quality Switch
                            TextButton(onClick = { showQualityMenu = true }) {
                                Text("Quality: ${selectedResolution}", color = PrimaryAccent, fontWeight = FontWeight.Bold)
                            }

                            // Speed control
                            TextButton(onClick = { showSpeedMenu = true }) {
                                Text("Speed: ${selectedSpeed}x", color = Color.White)
                            }
                        }

                        // Picture in Picture Simulation
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.PictureInPicture, contentDescription = "PiP", tint = Color.White)
                        }
                    }
                }
            }
        }

        // DROP-DOWN MENUS SIMULATIONS
        if (showQualityMenu) {
            AlertDialog(
                onDismissRequest = { showQualityMenu = false },
                title = { Text("Select Resolution Stream", color = Color.White) },
                containerColor = CardSlateBackground,
                text = {
                    Column {
                        listOf("360p", "480p", "720p", "1080p (HQ)", "4K UHD").forEach { res ->
                            Text(
                                text = res,
                                color = if (selectedResolution == res.substringBefore(" ")) PrimaryAccent else Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedResolution = res.substringBefore(" ")
                                        showQualityMenu = false
                                    }
                                    .padding(vertical = 12.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showQualityMenu = false }) {
                        Text("Dismiss", color = Color.Gray)
                    }
                }
            )
        }

        if (showSpeedMenu) {
            AlertDialog(
                onDismissRequest = { showSpeedMenu = false },
                title = { Text("Select Playback Speed", color = Color.White) },
                containerColor = CardSlateBackground,
                text = {
                    Column {
                        listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                            Text(
                                text = "${speed}x",
                                color = if (selectedSpeed == speed) PrimaryAccent else Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedSpeed = speed
                                        showSpeedMenu = false
                                    }
                                    .padding(vertical = 12.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSpeedMenu = false }) {
                        Text("Dismiss", color = Color.Gray)
                    }
                }
            )
        }

        if (showAudioMenu) {
            AlertDialog(
                onDismissRequest = { showAudioMenu = false },
                title = { Text("Select Audio Stream Track", color = Color.White) },
                containerColor = CardSlateBackground,
                text = {
                    Column {
                        listOf("English (5.1 Atmos)", "Spanish (Dubbed)", "Japanese (Original Vocal)", "French (Stereo)").forEach { track ->
                            Text(
                                text = track,
                                color = if (selectedAudioTrack == track) PrimaryAccent else Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAudioTrack = track
                                        showAudioMenu = false
                                    }
                                    .padding(vertical = 12.dp),
                                fontSize = 16.sp
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAudioMenu = false }) {
                        Text("Dismiss", color = Color.Gray)
                    }
                }
            )
        }
    }
}

private fun formatTime(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hrs > 0) {
        String.format("%d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}
