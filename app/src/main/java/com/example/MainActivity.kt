package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MediaContent
import com.example.data.Repository
import com.example.ui.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val repository = remember { Repository(context) }

            // Top-Level Screen Routing States
            // "Splash", "Login", "Signup", "ForgotPassword", "ProfileSelection", "Dashboard", "MediaDetails", "VideoPlayer", "AdminPanel"
            var currentScreen by remember { mutableStateOf("Splash") }
            var selectedMedia by remember { mutableStateOf<MediaContent?>(null) }

            // Flow observations
            val mediaList by repository.allMedia.collectAsState(initial = emptyList())
            val profilesList by repository.profiles.collectAsState(initial = emptyList())

            val activeProfile = repository.currentProfile.value
            val watchlist by if (activeProfile != null) {
                repository.getWatchlist(activeProfile.id).collectAsState(initial = emptyList())
            } else {
                remember { mutableStateOf(emptyList()) }
            }

            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepSlateBackground
                ) {
                    when (currentScreen) {
                        "Splash" -> {
                            SplashScreen(
                                onSplashComplete = {
                                    val remembered = repository.currentUser.value
                                    if (remembered != null) {
                                        currentScreen = "ProfileSelection"
                                    } else {
                                        currentScreen = "Login"
                                    }
                                }
                            )
                        }

                        "Login" -> {
                            LoginScreen(
                                repository = repository,
                                onLoginSuccess = {
                                    currentScreen = "ProfileSelection"
                                },
                                onNavigateToSignup = { currentScreen = "Signup" },
                                onNavigateToForgotPassword = { currentScreen = "ForgotPassword" }
                            )
                        }

                        "Signup" -> {
                            SignupScreen(
                                repository = repository,
                                onSignupSuccess = {
                                    currentScreen = "ProfileSelection"
                                },
                                onNavigateToLogin = { currentScreen = "Login" }
                            )
                        }

                        "ForgotPassword" -> {
                            ForgotPasswordScreen(
                                repository = repository,
                                onResetComplete = {
                                    currentScreen = "ProfileSelection"
                                },
                                onBackToLogin = { currentScreen = "Login" }
                            )
                        }

                        "ProfileSelection" -> {
                            ProfileSelectionScreen(
                                repository = repository,
                                onProfileSelected = { profile ->
                                    repository.currentProfile.value = profile
                                    currentScreen = "Dashboard"
                                }
                            )
                        }

                        "Dashboard" -> {
                            DashboardContainer(
                                repository = repository,
                                mediaList = mediaList,
                                watchlist = watchlist,
                                onMediaSelected = { media ->
                                    selectedMedia = media
                                    currentScreen = "MediaDetails"
                                },
                                onPlayTriggered = { media ->
                                    selectedMedia = media
                                    currentScreen = "VideoPlayer"
                                },
                                onSwitchProfile = {
                                    repository.currentProfile.value = null
                                    currentScreen = "ProfileSelection"
                                },
                                onLogoutTriggered = {
                                    repository.currentUser.value = null
                                    repository.currentProfile.value = null
                                    currentScreen = "Login"
                                },
                                onOpenAdminPanel = {
                                    currentScreen = "AdminPanel"
                                }
                            )
                        }

                        "MediaDetails" -> {
                            selectedMedia?.let { media ->
                                MediaDetailsPage(
                                    repository = repository,
                                    media = media,
                                    watchlist = watchlist,
                                    onBack = { currentScreen = "Dashboard" },
                                    onPlay = { med ->
                                        selectedMedia = med
                                        currentScreen = "VideoPlayer"
                                    }
                                )
                            }
                        }

                        "VideoPlayer" -> {
                            selectedMedia?.let { media ->
                                VideoPlayerScreen(
                                    media = media,
                                    onClose = { currentScreen = "Dashboard" }
                                )
                            }
                        }

                        "AdminPanel" -> {
                            AdminPanelScreen(
                                repository = repository,
                                mediaList = mediaList,
                                onClose = { currentScreen = "Dashboard" }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// Main User App Dashboard & Navigation
// ==========================================
@Composable
fun DashboardContainer(
    repository: Repository,
    mediaList: List<MediaContent>,
    watchlist: List<com.example.data.WatchlistItem>,
    onMediaSelected: (MediaContent) -> Unit,
    onPlayTriggered: (MediaContent) -> Unit,
    onSwitchProfile: () -> Unit,
    onLogoutTriggered: () -> Unit,
    onOpenAdminPanel: () -> Unit
) {
    var activeTab by remember { mutableStateOf("Home") }
    val activeProfile = repository.currentProfile.value

    // Left sidebar vs bottom bar layout check (Responsive design support!)
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = DeepSlateBackground,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                val navItems = listOf(
                    Triple("Home", Icons.Default.Home, Icons.Outlined.Home),
                    Triple("Search", Icons.Default.Search, Icons.Outlined.Search),
                    Triple("Movies", Icons.Default.Movie, Icons.Outlined.Movie),
                    Triple("Series", Icons.Default.VideoLibrary, Icons.Outlined.VideoLibrary),
                    Triple("Anime", Icons.Default.Toys, Icons.Outlined.Toys),
                    Triple("Downloads", Icons.Default.Download, Icons.Outlined.Download),
                    Triple("Watchlist", Icons.Default.Bookmark, Icons.Outlined.BookmarkBorder),
                    Triple("Profile", Icons.Default.Person, Icons.Outlined.Person)
                )

                navItems.forEach { (name, filledIcon, outlinedIcon) ->
                    val isSelected = activeTab == name
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeTab = name },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) filledIcon else outlinedIcon,
                                contentDescription = name,
                                tint = if (isSelected) PrimaryAccent else Color.White
                            )
                        },
                        label = {
                            Text(
                                text = name,
                                fontSize = 10.sp,
                                color = if (isSelected) PrimaryAccent else Color.White
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = RosePremium.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            // Secret float trigger button to activate admin panel at any time
            FloatingActionButton(
                onClick = onOpenAdminPanel,
                containerColor = RosePremium,
                contentColor = Color.White,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("open_admin_panel_fab")
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Panel")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DeepSlateBackground)
        ) {
            when (activeTab) {
                "Home" -> {
                    HomeTab(
                        repository = repository,
                        mediaList = mediaList,
                        watchlist = watchlist,
                        onMediaSelected = onMediaSelected,
                        onPlayTriggered = onPlayTriggered
                    )
                }

                "Search" -> {
                    SearchTab(
                        repository = repository,
                        mediaList = mediaList,
                        watchlist = watchlist,
                        onMediaSelected = onMediaSelected
                    )
                }

                "Movies" -> {
                    // Movies-only Specialty Grid Screen
                    val moviesOnly = mediaList.filter { it.type == "Movie" }
                    SpecialtyGridTab(
                        title = "Blockbuster Movies",
                        subtitle = "Only the highest bitrate cinemas streamed locally",
                        mediaList = moviesOnly,
                        watchlist = watchlist,
                        repository = repository,
                        onMediaSelected = onMediaSelected
                    )
                }

                "Series" -> {
                    // Series Specialty Grid Screen
                    val seriesOnly = mediaList.filter { it.type == "Series" }
                    SpecialtyGridTab(
                        title = "Binge-Worthy TV Series",
                        subtitle = "Complete seasons, full audio controls, and recap skip",
                        mediaList = seriesOnly,
                        watchlist = watchlist,
                        repository = repository,
                        onMediaSelected = onMediaSelected
                    )
                }

                "Anime" -> {
                    // Anime Specialty Screen with distinct Sub/Dub labels
                    val animeOnly = mediaList.filter { it.type == "Anime" }
                    AnimeSpecialtyGridTab(
                        title = "Trending Anime Sector",
                        mediaList = animeOnly,
                        watchlist = watchlist,
                        repository = repository,
                        onMediaSelected = onMediaSelected
                    )
                }

                "Downloads" -> {
                    DownloadsTab(
                        repository = repository,
                        onPlayOffline = { download ->
                            // Play the offline download by looking up its associated MediaContent
                            val media = mediaList.find { it.id == download.mediaId }
                            if (media != null) {
                                onPlayTriggered(media)
                            }
                        }
                    )
                }

                "Watchlist" -> {
                    WatchlistTab(
                        repository = repository,
                        mediaList = mediaList,
                        watchlist = watchlist,
                        onMediaSelected = onMediaSelected
                    )
                }

                "Profile" -> {
                    ProfileTab(
                        repository = repository,
                        onSwitchProfile = onSwitchProfile,
                        onLogoutTriggered = onLogoutTriggered
                    )
                }
            }
        }
    }
}

// ==========================================
// 9. Specialty Content Grids (Movies / Series)
// ==========================================
@Composable
fun SpecialtyGridTab(
    title: String,
    subtitle: String,
    mediaList: List<MediaContent>,
    watchlist: List<com.example.data.WatchlistItem>,
    repository: Repository,
    onMediaSelected: (MediaContent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground)
            .padding(16.dp)
    ) {
        Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = GraySecondary, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(16.dp))

        if (mediaList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No titles uploaded in this category.", color = GraySecondary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(135.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 60.dp)
            ) {
                items(mediaList) { media ->
                    val isFav = watchlist.any { it.mediaId == media.id }
                    MediaCard(
                        media = media,
                        onClick = { onMediaSelected(media) },
                        onFavoriteToggle = {
                            repository.currentProfile.value?.let { prof ->
                                coroutineScope.launch {
                                    repository.toggleWatchlist(media.id, prof.id)
                                }
                            }
                        },
                        onDownloadTrigger = {
                            repository.currentProfile.value?.let { prof ->
                                coroutineScope.launch {
                                    repository.startDownload(media.id, null, prof.id, media.title, "1080p")
                                }
                            }
                        },
                        isFavorite = isFav
                    )
                }
            }
        }
    }
}

// ==========================================
// 10. Anime Specialty Grid with Sub/Dub labels
// ==========================================
@Composable
fun AnimeSpecialtyGridTab(
    title: String,
    mediaList: List<MediaContent>,
    watchlist: List<com.example.data.WatchlistItem>,
    repository: Repository,
    onMediaSelected: (MediaContent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground)
            .padding(16.dp)
    ) {
        Text(title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Japanese voice acting with dual-language subtitles & dubbed audio tracks.", color = GraySecondary, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(16.dp))

        if (mediaList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No anime titles uploaded yet.", color = GraySecondary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(145.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 60.dp)
            ) {
                items(mediaList) { media ->
                    val isFav = watchlist.any { it.mediaId == media.id }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardSlateBackground),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMediaSelected(media) }
                            .testTag("anime_card_${media.title}")
                    ) {
                        Box {
                            CinemaPoster(
                                title = media.title,
                                type = "Anime",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(190.dp)
                            )

                            // Dub / Sub Dual labels
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("SUB | DUB", color = PrimaryAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }

                            // Ongoing status label
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(if (media.isOngoing) Color.Green else RosePremium, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (media.isOngoing) "ONGOING" else "FINISHED",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(media.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Rating: ★ ${media.rating}", color = RatingGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("4K UHD", color = GraySecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
