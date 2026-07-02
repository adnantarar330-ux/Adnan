package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MediaContent
import com.example.data.Repository
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AdminPanelScreen(
    repository: Repository,
    mediaList: List<MediaContent>,
    onClose: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedAdminTab by remember { mutableStateOf("Dashboard") }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGreyBackground)
    ) {
        // ADMIN SIDEBAR NAVIGATION
        Column(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .background(DeepSlateBackground)
                .padding(12.dp)
        ) {
            // Admin Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            ) {
                Icon(Icons.Default.SettingsInputComponent, contentDescription = "Console", tint = RosePremium, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("CS-CONSOLE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            Divider(color = CardSlateBackground, modifier = Modifier.padding(bottom = 16.dp))

            // Sidebar Tabs
            val tabs = listOf("Dashboard", "Content Mgr", "Users List", "Categories", "Settings")
            tabs.forEach { tab ->
                val isSelected = selectedAdminTab == tab
                Button(
                    onClick = { selectedAdminTab = tab },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) RosePremium else Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (tab) {
                                "Dashboard" -> Icons.Default.Dashboard
                                "Content Mgr" -> Icons.Default.CloudUpload
                                "Users List" -> Icons.Default.People
                                "Categories" -> Icons.Default.Category
                                else -> Icons.Default.Settings
                            },
                            contentDescription = tab,
                            tint = if (isSelected) Color.White else PrimaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = tab,
                            color = if (isSelected) Color.White else GraySecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Exit Console Button
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = CardSlateBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exit_admin_panel"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Exit Console", color = Color.White, fontSize = 12.sp)
            }
        }

        // TAB DETAILS
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            when (selectedAdminTab) {
                "Dashboard" -> AdminDashboardView(mediaList)
                "Content Mgr" -> AdminContentManagerView(repository, mediaList)
                "Users List" -> AdminUsersListView()
                "Categories" -> AdminCategoriesView()
                else -> AdminSettingsView()
            }
        }
    }
}

// ==========================================
// 1. Dashboard Tab View
// ==========================================
@Composable
fun AdminDashboardView(mediaList: List<MediaContent>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Console Overview Dashboard", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Real-time telemetry feeds & financial stream trackers.", color = GraySecondary, fontSize = 12.sp)
        }

        // Stats Row
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                DashboardStatCard("Active Viewers", "1,248", "● Live", RosePremium, Modifier.weight(1f))
                DashboardStatCard("Premium VIPs", "4,821", "+14% MoM", PrimaryAccent, Modifier.weight(1f))
                DashboardStatCard("Titles Online", mediaList.size.toString(), "Active", RatingGold, Modifier.weight(1f))
                DashboardStatCard("Monthly Revenue", "$48,912", "+8.4%", Color.Green, Modifier.weight(1f))
            }
        }

        // Custom Graphs Row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Monthly Revenue Trends (USD)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Draw Line Graph using Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        val points = listOf(
                            Offset(0f, 120f),
                            Offset(150f, 90f),
                            Offset(300f, 100f),
                            Offset(450f, 50f),
                            Offset(600f, 40f),
                            Offset(750f, 10f)
                        )
                        val path = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }
                        // Draw guide lines
                        drawLine(Color.Gray.copy(alpha = 0.2f), Offset(0f, 120f), Offset(size.width, 120f))
                        drawLine(Color.Gray.copy(alpha = 0.2f), Offset(0f, 60f), Offset(size.width, 60f))

                        drawPath(
                            path = path,
                            color = PrimaryAccent,
                            style = Stroke(width = 6f)
                        )

                        // Draw points
                        points.forEach { pt ->
                            drawCircle(RosePremium, radius = 6f, center = pt)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Jan", color = GraySecondary, fontSize = 11.sp)
                        Text("Mar", color = GraySecondary, fontSize = 11.sp)
                        Text("May", color = GraySecondary, fontSize = 11.sp)
                        Text("Jul", color = GraySecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardStatCard(title: String, value: String, status: String, tint: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardSlateBackground),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = GraySecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(tint.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(status, color = tint, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// 2. Content Manager View (Upload Content)
// ==========================================
@Composable
fun AdminContentManagerView(repository: Repository, mediaList: List<MediaContent>) {
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var contentType by remember { mutableStateOf("Movie") } // Movie, Series, Anime
    var genre by remember { mutableStateOf("Action, Drama") }
    var releaseYear by remember { mutableStateOf("2026") }
    var duration by remember { mutableStateOf("1h 55m") }
    var rating by remember { mutableStateOf("4.7") }
    var quality by remember { mutableStateOf("4K") }
    var director by remember { mutableStateOf("Christopher Nolan") }
    var cast by remember { mutableStateOf("Christian Bale, Heath Ledger") }
    var language by remember { mutableStateOf("English (Dolby 5.1)") }
    var isPremium by remember { mutableStateOf(false) }
    var isFeatured by remember { mutableStateOf(false) }

    var feedbackMessage by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upload form
        Column(
            modifier = Modifier
                .weight(1.2f)
                .verticalScroll(rememberScrollState())
                .padding(end = 8.dp)
        ) {
            Text("Upload Movie / Series / Anime Content", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (feedbackMessage.isNotEmpty()) {
                Text(feedbackMessage, color = Color.Green, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Content Title", color = GraySecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Synopsis", color = GraySecondary) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = CardSlateBackground)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Type selectors
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Movie", "Series", "Anime").forEach { type ->
                    FilterChip(
                        selected = contentType == type,
                        onClick = { contentType = type },
                        label = { Text(type) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RosePremium, selectedLabelColor = Color.White)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Director & Cast
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = director,
                    onValueChange = { director = it },
                    label = { Text("Director", color = GraySecondary) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PrimaryAccent)
                )
                OutlinedTextField(
                    value = cast,
                    onValueChange = { cast = it },
                    label = { Text("Cast Members", color = GraySecondary) },
                    modifier = Modifier.weight(1.2f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PrimaryAccent)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Metadata Row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre", color = GraySecondary) },
                    modifier = Modifier.weight(1.2f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                OutlinedTextField(
                    value = releaseYear,
                    onValueChange = { releaseYear = it },
                    label = { Text("Year", color = GraySecondary) },
                    modifier = Modifier.weight(0.8f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration", color = GraySecondary) },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Switches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPremium, onCheckedChange = { isPremium = it })
                    Text("Premium VIP Locked", color = Color.White, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
                    Text("Hero Slider Featured", color = Color.White, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isBlank()) {
                        feedbackMessage = "Error: Title cannot be empty!"
                        return@Button
                    }
                    val yr = releaseYear.toIntOrNull() ?: 2026
                    val rat = rating.toFloatOrNull() ?: 4.8f

                    val newMedia = MediaContent(
                        title = title,
                        description = description,
                        poster = "poster_uploaded",
                        banner = "banner_uploaded",
                        trailerUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                        videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        type = contentType,
                        rating = rat,
                        quality = quality,
                        genre = genre,
                        releaseYear = yr,
                        duration = duration,
                        director = director,
                        cast = cast,
                        language = language,
                        subAvailable = true,
                        isPremium = isPremium,
                        isFeatured = isFeatured
                    )

                    coroutineScope.launch {
                        repository.addContent(newMedia)
                        feedbackMessage = "Successfully Uploaded content to DB!"
                        title = ""
                        description = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("upload_submit_button"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
            ) {
                Text("Publish to App Catalog", color = DeepSlateBackground, fontWeight = FontWeight.Bold)
            }
        }

        // Live library view on the right
        Column(
            modifier = Modifier.weight(0.8f)
        ) {
            Text("Published Content Library", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(mediaList) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(item.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Type: ${item.type} | Gen: ${item.genre}", color = GraySecondary, fontSize = 11.sp)
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch { repository.deleteContent(item) }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RosePremium)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. Simple Users List view
// ==========================================
@Composable
fun AdminUsersListView() {
    Column {
        Text("Registered Users Accounts", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        listOf(
            "adnantarar04182@gmail.com" to "Premium Active",
            "google.user@gmail.com" to "Premium Active",
            "fb.user@facebook.com" to "Free Account",
            "guest.user@cinestream.com" to "Guest Level"
        ).forEach { (email, status) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = "User", tint = PrimaryAccent)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(email, color = Color.White, fontSize = 14.sp)
                    }

                    Box(
                        modifier = Modifier
                            .background(if (status.contains("Premium")) Color.Green.copy(alpha = 0.2f) else CardSlateBackground)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(status, color = if (status.contains("Premium")) Color.Green else GraySecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. Categories management
// ==========================================
@Composable
fun AdminCategoriesView() {
    Column {
        Text("Active Genre & Categories Hub", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        listOf(
            "Sci-Fi & Astronomy",
            "Crime & Noir Thrillers",
            "Anime Fantasy & Isekai",
            "CineStream Original Masterworks",
            "Classic Comic Action Adventure",
            "Atmospheric Horrors"
        ).forEach { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(category, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryAccent, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ==========================================
// 5. Admin settings view
// ==========================================
@Composable
fun AdminSettingsView() {
    Column {
        Text("Admin Console Configurations", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        listOf(
            "Adaptive Streaming Dual-Bitrates",
            "Dolby Digital Passthrough",
            "CDN Video Server Fallbacks",
            "Sandbox Telemetry Debug Logs"
        ).forEach { config ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(config, color = Color.White, fontSize = 14.sp)
                    Switch(checked = true, onCheckedChange = {})
                }
            }
        }
    }
}
