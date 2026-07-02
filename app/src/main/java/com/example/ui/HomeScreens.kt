package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ==========================================
// 1. Unified Poster Component (High-End Custom Drawing)
// ==========================================
@Composable
fun CinemaPoster(
    title: String,
    type: String,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false,
    enableKenBurns: Boolean = false
) {
    // Elegant background gradient based on title hashing
    val gradientColors = remember(title) {
        val hash = title.hashCode().coerceAtLeast(0)
        val themes = listOf(
            listOf(Color(0xFF3F2B96), Color(0xFFA8C0FF)),
            listOf(Color(0xFF11998E), Color(0xFF38EF7D)),
            listOf(Color(0xFFFC466B), Color(0xFF3F5EFB)),
            listOf(Color(0xFF1F1C2C), Color(0xFF928DAB)),
            listOf(Color(0xFFF12711), Color(0xFFF5AF19)),
            listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCB045))
        )
        themes[hash % themes.size]
    }

    val infiniteTransition = rememberInfiniteTransition(label = "KenBurns")
    val scaleFactor by if (enableKenBurns) {
        infiniteTransition.animateFloat(
            initialValue = 1.00f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 14000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "ScaleFactor"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }

    val translationX by if (enableKenBurns) {
        infiniteTransition.animateFloat(
            initialValue = -12f,
            targetValue = 12f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 18000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "TranslationX"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    val translationY by if (enableKenBurns) {
        infiniteTransition.animateFloat(
            initialValue = -6f,
            targetValue = 6f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 20000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "TranslationY"
        )
    } else {
        remember { mutableStateOf(0f) }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .graphicsLayer {
                if (enableKenBurns) {
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                    this.translationX = translationX
                    this.translationY = translationY
                }
            }
            .background(Brush.verticalGradient(gradientColors))
            .drawBehind {
                // Paint modern abstract shapes
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = size.width * 0.6f,
                    center = Offset(size.width * 0.1f, size.height * 0.2f)
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.1f),
                    start = Offset(0f, size.height * 0.7f),
                    end = Offset(size.width, size.height * 0.6f),
                    strokeWidth = 3f
                )
            },
        contentAlignment = Alignment.BottomStart
    ) {
        // Overlay film tint
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )

        // Title and Type Tags
        Column(
            modifier = Modifier.padding(if (isLarge) 16.dp else 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(RosePremium, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = type.uppercase(),
                    color = Color.White,
                    fontSize = if (isLarge) 11.sp else 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = if (isLarge) 20.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ==========================================
// 2. Movie/Show Card Component
// ==========================================
@Composable
fun MediaCard(
    media: MediaContent,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onDownloadTrigger: () -> Unit,
    isFavorite: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(150.dp)
            .padding(4.dp)
            .clickable { onClick() }
            .testTag("media_card_${media.title}"),
        colors = CardDefaults.cardColors(containerColor = CardSlateBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            CinemaPoster(
                title = media.title,
                type = media.type,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            // Rating Gold Badge
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating",
                    tint = RatingGold,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = media.rating.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quality and Premium Badge Row
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (media.isPremium) {
                    Box(
                        modifier = Modifier
                            .background(RosePremium, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("PREM", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(PrimaryAccent, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(media.quality, color = DeepSlateBackground, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Interactive Buttons on Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Heart Icon
                IconButton(
                    onClick = { onFavoriteToggle() },
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) RosePremium else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Download Icon
                IconButton(
                    onClick = { onDownloadTrigger() },
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download",
                        tint = PrimaryAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. Home Tab
// ==========================================
@Composable
fun HomeTab(
    repository: Repository,
    mediaList: List<MediaContent>,
    watchlist: List<WatchlistItem>,
    onMediaSelected: (MediaContent) -> Unit,
    onPlayTriggered: (MediaContent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val activeProfile = repository.currentProfile.value
    val isPremiumUser = repository.currentUser.value?.isPremium ?: false

    // State for streaming the trailer from the slider
    var activeTrailerMedia by remember { mutableStateOf<MediaContent?>(null) }
    val featuredList = mediaList.filter { it.isFeatured }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground)
    ) {
        // HERO BANNER CAROUSEL SLIDER
        if (featuredList.isNotEmpty()) {
            item {
                val pagerState = rememberPagerState(pageCount = { featuredList.size })

                // Auto-sliding Coroutine inside LaunchedEffect
                LaunchedEffect(pagerState) {
                    while (true) {
                        delay(6000)
                        if (featuredList.isNotEmpty()) {
                            val nextPage = (pagerState.currentPage + 1) % featuredList.size
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp) // Extra height for premium aesthetics
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        val bannerMedia = featuredList[page]
                        
                        // Page transformation effect for a fluid 3D carousel aesthetic
                        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        val scale = (1f - (kotlin.math.abs(pageOffset) * 0.12f)).coerceIn(0.85f, 1f)
                        val alpha = (1f - (kotlin.math.abs(pageOffset) * 0.4f)).coerceIn(0.6f, 1f)

                        // Staggered fade-in and slide-up transition states
                        val isPageActive = pagerState.currentPage == page
                        
                        val titleAlpha by animateFloatAsState(
                            targetValue = if (isPageActive) 1f else 0f,
                            animationSpec = tween(durationMillis = 800, delayMillis = 0, easing = FastOutSlowInEasing),
                            label = "TitleAlpha"
                        )
                        val titleTranslationY by animateFloatAsState(
                            targetValue = if (isPageActive) 0f else 25f,
                            animationSpec = tween(durationMillis = 800, delayMillis = 0, easing = FastOutSlowInEasing),
                            label = "TitleTranslation"
                        )

                        val metaAlpha by animateFloatAsState(
                            targetValue = if (isPageActive) 1f else 0f,
                            animationSpec = tween(durationMillis = 800, delayMillis = 150, easing = FastOutSlowInEasing),
                            label = "MetaAlpha"
                        )
                        val metaTranslationY by animateFloatAsState(
                            targetValue = if (isPageActive) 0f else 25f,
                            animationSpec = tween(durationMillis = 800, delayMillis = 150, easing = FastOutSlowInEasing),
                            label = "MetaTranslation"
                        )

                        val actionsAlpha by animateFloatAsState(
                            targetValue = if (isPageActive) 1f else 0f,
                            animationSpec = tween(durationMillis = 800, delayMillis = 300, easing = FastOutSlowInEasing),
                            label = "ActionsAlpha"
                        )
                        val actionsTranslationY by animateFloatAsState(
                            targetValue = if (isPageActive) 0f else 25f,
                            animationSpec = tween(durationMillis = 800, delayMillis = 300, easing = FastOutSlowInEasing),
                            label = "ActionsTranslation"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    this.alpha = alpha
                                }
                        ) {
                            CinemaPoster(
                                title = bannerMedia.title,
                                type = bannerMedia.type,
                                modifier = Modifier.fillMaxSize(),
                                isLarge = true,
                                enableKenBurns = isPageActive
                            )

                            // Cinematic multi-layered gradient overlays (Vignette)
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.5f),
                                                Color.Transparent,
                                                DeepSlateBackground.copy(alpha = 0.95f),
                                                DeepSlateBackground
                                            )
                                        )
                                    )
                            )

                            // Meta Info Overlay Content
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(horizontal = 20.dp, vertical = 24.dp)
                            ) {
                                // Title with premium styling and fade-in/slide-up animation
                                Text(
                                    text = bannerMedia.title,
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.graphicsLayer {
                                        this.alpha = titleAlpha
                                        this.translationY = titleTranslationY
                                    }
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Metadata row with staggered animation
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.graphicsLayer {
                                        this.alpha = metaAlpha
                                        this.translationY = metaTranslationY
                                    }
                                ) {
                                    // Rating
                                    Box(
                                        modifier = Modifier
                                            .background(RatingGold, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "★ ${bannerMedia.rating}",
                                            color = DeepSlateBackground,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    // Quality badge
                                    Box(
                                        modifier = Modifier
                                            .background(PrimaryAccent.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .border(1.dp, PrimaryAccent, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = bannerMedia.quality,
                                            color = PrimaryAccent,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Text(
                                        text = "${bannerMedia.releaseYear}",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        text = bannerMedia.duration,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        text = bannerMedia.genre,
                                        color = PrimaryAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Interactive Action Row with staggered animation
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.graphicsLayer {
                                        this.alpha = actionsAlpha
                                        this.translationY = actionsTranslationY
                                    }
                                ) {
                                    // Play Now Button
                                    Button(
                                        onClick = { onPlayTriggered(bannerMedia) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                        modifier = Modifier.height(40.dp)
                                    ) {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = DeepSlateBackground, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Play Now", color = DeepSlateBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    // Watch Trailer Button
                                    Button(
                                        onClick = { activeTrailerMedia = bannerMedia },
                                        colors = ButtonDefaults.buttonColors(containerColor = RosePremium.copy(alpha = 0.9f)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                        modifier = Modifier.height(40.dp)
                                    ) {
                                        Icon(Icons.Filled.Movie, contentDescription = "Trailer", tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Trailer", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    // Details Button
                                    Button(
                                        onClick = { onMediaSelected(bannerMedia) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CardSlateBackground.copy(alpha = 0.8f)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                        modifier = Modifier.height(40.dp)
                                    ) {
                                        Icon(Icons.Outlined.Info, contentDescription = "Info", tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Details", color = Color.White, fontSize = 13.sp)
                                    }

                                    // Watchlist Toggle IconButton
                                    IconButton(
                                        onClick = {
                                            activeProfile?.let { prof ->
                                                coroutineScope.launch {
                                                    repository.toggleWatchlist(bannerMedia.id, prof.id)
                                                }
                                            }
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(CardSlateBackground.copy(alpha = 0.8f), CircleShape)
                                    ) {
                                        val isFav = watchlist.any { it.mediaId == bannerMedia.id }
                                        Icon(
                                            imageVector = if (isFav) Icons.Filled.Check else Icons.Default.Add,
                                            contentDescription = "Watchlist",
                                            tint = PrimaryAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Indicator Dots Overlay (Bottom Center of Carousel)
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 10.dp)
                            .height(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(featuredList.size) { iteration ->
                            val isSelected = pagerState.currentPage == iteration
                            val dotColor = if (isSelected) PrimaryAccent else Color.White.copy(alpha = 0.35f)
                            val dotWidth = if (isSelected) 18.dp else 6.dp
                            
                            // Visual feedback animation simulation
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                                    .width(dotWidth)
                                    .height(6.dp)
                            )
                        }
                    }
                }
            }
        }

        // SECTIONS
        // Trending Now
        item {
            MediaRowSection(
                title = "Trending Now 🔥",
                mediaList = mediaList.shuffled(),
                watchlist = watchlist,
                repository = repository,
                onMediaSelected = onMediaSelected
            )
        }

        // Popular Movies
        item {
            MediaRowSection(
                title = "Popular Movies 🎬",
                mediaList = mediaList.filter { it.type == "Movie" },
                watchlist = watchlist,
                repository = repository,
                onMediaSelected = onMediaSelected
            )
        }

        // Anime Picks
        item {
            MediaRowSection(
                title = "Anime Picks 🌸",
                mediaList = mediaList.filter { it.type == "Anime" },
                watchlist = watchlist,
                repository = repository,
                onMediaSelected = onMediaSelected
            )
        }

        // Originals
        item {
            MediaRowSection(
                title = "CineStream Originals 💎",
                mediaList = mediaList.filter { it.isPremium },
                watchlist = watchlist,
                repository = repository,
                onMediaSelected = onMediaSelected
            )
        }

        // Recommended / Smart AI recommendations
        item {
            MediaRowSection(
                title = "Smart AI Recommendations For You ✨",
                mediaList = mediaList.sortedByDescending { it.rating },
                watchlist = watchlist,
                repository = repository,
                onMediaSelected = onMediaSelected
            )
        }

        // Bottom space padding
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    // Trailer video playback stream dialog overlay
    activeTrailerMedia?.let { media ->
        TrailerStreamDialog(
            media = media,
            onClose = { activeTrailerMedia = null }
        )
    }
}

@Composable
fun MediaRowSection(
    title: String,
    mediaList: List<MediaContent>,
    watchlist: List<WatchlistItem>,
    repository: Repository,
    onMediaSelected: (MediaContent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val activeProfile = repository.currentProfile.value

    if (mediaList.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mediaList) { media ->
                    val isFav = watchlist.any { it.mediaId == media.id }
                    MediaCard(
                        media = media,
                        onClick = { onMediaSelected(media) },
                        onFavoriteToggle = {
                            activeProfile?.let { prof ->
                                coroutineScope.launch {
                                    repository.toggleWatchlist(media.id, prof.id)
                                }
                            }
                        },
                        onDownloadTrigger = {
                            activeProfile?.let { prof ->
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
// 4. Search Tab
// ==========================================
@Composable
fun SearchTab(
    repository: Repository,
    mediaList: List<MediaContent>,
    watchlist: List<WatchlistItem>,
    onMediaSelected: (MediaContent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("All") }
    var selectedYear by remember { mutableStateOf("All") }
    var selectedType by remember { mutableStateOf("All") } // All, Movie, Series, Anime

    val genres = listOf("All", "Sci-Fi", "Action", "Drama", "Adventure", "Crime", "Thriller", "Anime", "Fantasy")
    val years = listOf("All", "2024", "2021", "2020", "2019", "2016", "2014", "2010", "2008")

    val searchResults = mediaList.filter { media ->
        val matchesQuery = media.title.contains(searchQuery, ignoreCase = true) ||
                media.director.contains(searchQuery, ignoreCase = true) ||
                media.cast.contains(searchQuery, ignoreCase = true)

        val matchesGenre = selectedGenre == "All" || media.genre.contains(selectedGenre, ignoreCase = true)
        val matchesYear = selectedYear == "All" || media.releaseYear.toString() == selectedYear
        val matchesType = selectedType == "All" || media.type.equals(selectedType, ignoreCase = true)

        matchesQuery && matchesGenre && matchesYear && matchesType
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground)
            .padding(16.dp)
    ) {
        // Search Bar with input + mic icon
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search title, actor, director...", color = GraySecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = PrimaryAccent) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Search",
                    tint = PrimaryAccent,
                    modifier = Modifier.clickable { searchQuery = "Nolan" }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = CardSlateBackground
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Advanced filter horizontal rows
        Text("Advanced Multi-Filters", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        // Genre Filter
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(genres) { genre ->
                FilterChip(
                    selected = selectedGenre == genre,
                    onClick = { selectedGenre = genre },
                    label = { Text(genre, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryAccent,
                        selectedLabelColor = DeepSlateBackground,
                        containerColor = CardSlateBackground,
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Type filter row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("All", "Movie", "Series", "Anime").forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text(type, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RosePremium,
                        selectedLabelColor = Color.White,
                        containerColor = CardSlateBackground,
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Results Section
        Text(
            text = "Search Results (${searchResults.size})",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.MovieFilter, contentDescription = "Empty", tint = GraySecondary, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No results found", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Try looking up a different title or select standard genre", color = GraySecondary, fontSize = 12.sp)
                }
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
                items(searchResults) { media ->
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
// 5. Downloads Tab
// ==========================================
@Composable
fun DownloadsTab(
    repository: Repository,
    onPlayOffline: (DownloadItem) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val activeProfile = repository.currentProfile.value
    val downloadsList by if (activeProfile != null) {
        repository.getDownloads(activeProfile.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<DownloadItem>()) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground)
            .padding(16.dp)
    ) {
        Text("My Offline Downloads", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Take your premium videos anywhere. No internet required.", color = GraySecondary, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Simulated Storage Usage Indicator
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Storage, contentDescription = "Storage", tint = PrimaryAccent, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("CineStream Storage Usage", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { 0.35f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape),
                        color = PrimaryAccent,
                        trackColor = DeepSlateBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Free: 84.6 GB | CineStream Content: 4.8 GB", color = GraySecondary, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (downloadsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CloudDownload, contentDescription = "No Downloads", tint = GraySecondary, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No local content found", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Tap the download icon on any movie card to download offline.", color = GraySecondary, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 60.dp)
            ) {
                items(downloadsList) { download ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(PrimaryAccent, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Movie, contentDescription = "Movie", tint = DeepSlateBackground)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(download.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text("Quality: ${download.quality} | Size: ${download.totalSizeMb} MB", color = GraySecondary, fontSize = 12.sp)

                                Spacer(modifier = Modifier.height(8.dp))

                                if (download.isCompleted) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = Color.Green, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Completed & Verified", color = Color.Green, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        LinearProgressIndicator(
                                            progress = { download.progress / 100f },
                                            color = RosePremium,
                                            trackColor = DeepSlateBackground,
                                            modifier = Modifier
                                                .width(100.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Downloading: ${download.progress}%", color = RosePremium, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Row {
                                if (download.isCompleted) {
                                    IconButton(onClick = { onPlayOffline(download) }) {
                                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = PrimaryAccent)
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        coroutineScope.launch { repository.deleteDownload(download.id) }
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
}

// ==========================================
// 6. Watchlist Tab
// ==========================================
@Composable
fun WatchlistTab(
    repository: Repository,
    mediaList: List<MediaContent>,
    watchlist: List<WatchlistItem>,
    onMediaSelected: (MediaContent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val activeProfile = repository.currentProfile.value

    val savedContents = mediaList.filter { media ->
        watchlist.any { it.mediaId == media.id }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground)
            .padding(16.dp)
    ) {
        Text("My Watchlist", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Saved movies, series, and anime ready for viewing.", color = GraySecondary, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(16.dp))

        if (savedContents.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Empty Watchlist", tint = GraySecondary, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your watchlist is empty", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Save content from home screen or details page.", color = GraySecondary, fontSize = 12.sp)
                }
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
                items(savedContents) { media ->
                    MediaCard(
                        media = media,
                        onClick = { onMediaSelected(media) },
                        onFavoriteToggle = {
                            activeProfile?.let { prof ->
                                coroutineScope.launch {
                                    repository.toggleWatchlist(media.id, prof.id)
                                }
                            }
                        },
                        onDownloadTrigger = {
                            activeProfile?.let { prof ->
                                coroutineScope.launch {
                                    repository.startDownload(media.id, null, prof.id, media.title, "1080p")
                                }
                            }
                        },
                        isFavorite = true
                    )
                }
            }
        }
    }
}

// ==========================================
// 7. Profile Tab & App Settings
// ==========================================
@Composable
fun ProfileTab(
    repository: Repository,
    onSwitchProfile: () -> Unit,
    onLogoutTriggered: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val activeProfile = repository.currentProfile.value
    val userAccount = repository.currentUser.value

    var autoPlayState by remember { mutableStateOf(true) }
    var notificationState by remember { mutableStateOf(true) }
    var dataSaverState by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("English") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // PROFILE HEADER
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(PrimaryAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile", tint = DeepSlateBackground, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(activeProfile?.name ?: "Guest User", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Active Profile Level: ${activeProfile?.type ?: "Guest"}", color = GraySecondary, fontSize = 12.sp)
                        Text("Linked Account: ${userAccount?.email ?: "Offline Client"}", color = PrimaryAccent, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onSwitchProfile,
                        colors = ButtonDefaults.buttonColors(containerColor = RosePremium),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Switch", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // PREMIUM PLAN CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = RosePremium.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, RosePremium)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = "VIP", tint = RosePremium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CineStream Premium VIP", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (userAccount?.isPremium == true) "Your premium plan is active. Enjoy Dolby Atmos and true 4K streaming." else "Unlock all 4K contents, zero ads, dual-bitrate adaptive download support, and multi-profile setups.",
                        color = GraySecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (userAccount?.isPremium != true) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    repository.upgradePremium()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RosePremium),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upgrade to Premium VIP", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .background(Color.Green.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("PREMIUM ACTIVE", color = Color.Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // SETTINGS ROWS
        item {
            Text("App Preferences", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
            ) {
                Column {
                    // Autoplay toggle
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayCircleOutline, contentDescription = "Autoplay", tint = PrimaryAccent)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Auto Play Trailer / Episodes", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Start next episode automatically", color = GraySecondary, fontSize = 11.sp)
                            }
                        }
                        Switch(checked = autoPlayState, onCheckedChange = { autoPlayState = it })
                    }

                    Divider(color = DeepSlateBackground)

                    // Notifications Toggle
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notify", tint = PrimaryAccent)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("New Content Alerts", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Daily recommendations notifications", color = GraySecondary, fontSize = 11.sp)
                            }
                        }
                        Switch(checked = notificationState, onCheckedChange = { notificationState = it })
                    }

                    Divider(color = DeepSlateBackground)

                    // Data Saver Toggle
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NetworkWifi, contentDescription = "WiFi", tint = PrimaryAccent)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Data Saver Engine", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Optimizes playback to consume 60% less data", color = GraySecondary, fontSize = 11.sp)
                            }
                        }
                        Switch(checked = dataSaverState, onCheckedChange = { dataSaverState = it })
                    }
                }
            }
        }

        // LANGUAGE SELECTION
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("System Language", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("English", "Spanish", "French", "Japanese").forEach { lang ->
                            FilterChip(
                                selected = selectedLanguage == lang,
                                onClick = { selectedLanguage = lang },
                                label = { Text(lang) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryAccent,
                                    selectedLabelColor = DeepSlateBackground
                                )
                            )
                        }
                    }
                }
            }
        }

        // LOGOUT & BACK
        item {
            Button(
                onClick = onLogoutTriggered,
                colors = ButtonDefaults.buttonColors(containerColor = CardSlateBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("logout_button")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = RosePremium)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout Account", color = RosePremium, fontWeight = FontWeight.Bold)
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

// ==========================================
// 8. Media Details Page
// ==========================================
@Composable
fun MediaDetailsPage(
    repository: Repository,
    media: MediaContent,
    watchlist: List<WatchlistItem>,
    onBack: () -> Unit,
    onPlay: (MediaContent) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val activeProfile = repository.currentProfile.value
    val isFav = watchlist.any { it.mediaId == media.id }

    val episodesList by repository.getEpisodesForSeries(media.id).collectAsState(initial = emptyList())
    val reviewsList by repository.getReviews(media.id).collectAsState(initial = emptyList())

    var reviewText by remember { mutableStateOf("") }
    var reviewRating by remember { mutableIntStateOf(5) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBackground)
    ) {
        // Backdrop Image
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                CinemaPoster(
                    title = media.title,
                    type = media.type,
                    modifier = Modifier.fillMaxSize(),
                    isLarge = true
                )

                // Back arrow
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .align(Alignment.TopStart)
                        .testTag("details_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                // Shadow gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, DeepSlateBackground)
                            )
                        )
                )
            }
        }

        // META DATA
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = media.title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(RatingGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("★ ${media.rating}", color = DeepSlateBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Text(media.quality, color = PrimaryAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${media.releaseYear}", color = GraySecondary, fontSize = 12.sp)
                    Text(media.duration, color = GraySecondary, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons: Play & Download & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onPlay(media) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("play_now_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = DeepSlateBackground)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Play Now", color = DeepSlateBackground, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            activeProfile?.let { prof ->
                                coroutineScope.launch {
                                    repository.startDownload(media.id, null, prof.id, media.title, "1080p")
                                }
                            }
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("download_details_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CardSlateBackground),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Download", tint = PrimaryAccent)
                    }

                    Button(
                        onClick = {
                            activeProfile?.let { prof ->
                                coroutineScope.launch {
                                    repository.toggleWatchlist(media.id, prof.id)
                                }
                            }
                        },
                        modifier = Modifier
                            .height(50.dp)
                            .testTag("watchlist_details_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CardSlateBackground),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isFav) Icons.Filled.Check else Icons.Default.Add,
                            contentDescription = "Watchlist",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text("Synopsis", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(media.description, color = GraySecondary, fontSize = 13.sp, lineHeight = 18.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // Director & Cast Info
                Text("Cast & Crew", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Director: ${media.director}", color = PrimaryAccent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Cast: ${media.cast}", color = GraySecondary, fontSize = 13.sp)
            }
        }

        // EPISODES SECTION (For Series/Anime)
        if (media.type == "Series" || media.type == "Anime") {
            item {
                Text(
                    text = "Seasons & Episodes",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (episodesList.isEmpty()) {
                item {
                    Text("No episodes uploaded.", color = GraySecondary, modifier = Modifier.padding(16.dp))
                }
            } else {
                items(episodesList) { episode ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { onPlay(media) },
                        colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Video icon placeholder
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(DeepSlateBackground, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = "Play Episode", tint = PrimaryAccent)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Season ${episode.seasonNumber} Ep ${episode.episodeNumber}", color = PrimaryAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(episode.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(episode.description, color = GraySecondary, fontSize = 12.sp, maxLines = 2)
                            }
                        }
                    }
                }
            }
        }

        // SOCIAL COMMENTS & REVIEWS SECTION
        item {
            Divider(color = CardSlateBackground, modifier = Modifier.padding(vertical = 16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("User Reviews & Ratings", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                // Write a review field
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSlateBackground)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Add Your Review", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Star selection
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            (1..5).forEach { star ->
                                Icon(
                                    imageVector = if (star <= reviewRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "$star Stars",
                                    tint = RatingGold,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { reviewRating = star }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = reviewText,
                            onValueChange = { reviewText = it },
                            placeholder = { Text("Write something about this title...", color = GraySecondary, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = PrimaryAccent, unfocusedBorderColor = DeepSlateBackground)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (reviewText.isNotBlank()) {
                                    coroutineScope.launch {
                                        repository.addReview(
                                            mediaId = media.id,
                                            userName = activeProfile?.name ?: "Guest User",
                                            rating = reviewRating,
                                            comment = reviewText
                                        )
                                        reviewText = ""
                                        reviewRating = 5
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Submit Review", color = DeepSlateBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Review items loop
                if (reviewsList.isEmpty()) {
                    Text("No reviews yet. Be the first to share your rating!", color = GraySecondary, fontSize = 12.sp)
                } else {
                    reviewsList.forEach { review ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(review.userName, color = PrimaryAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Row {
                                    (1..review.rating).forEach {
                                        Icon(Icons.Filled.Star, contentDescription = "*", tint = RatingGold, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(review.comment, color = Color.White, fontSize = 13.sp)
                            Divider(color = CardSlateBackground, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun TrailerStreamDialog(
    media: MediaContent,
    onClose: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var currentSeconds by remember { mutableLongStateOf(0) }
    val totalSeconds = 120L // 2 Minutes trailer

    LaunchedEffect(key1 = isPlaying) {
        if (isPlaying) {
            while (currentSeconds < totalSeconds) {
                delay(1000)
                currentSeconds += 1
            }
        }
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // BACKGROUND/VISUAL
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                PrimaryAccent.copy(alpha = 0.25f),
                                Color.Black
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Movie,
                        contentDescription = "Trailer stream",
                        tint = RosePremium,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Streaming Official Trailer • ${media.title}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Text(
                        text = "Dolby Atmos 7.1 • CineStream CDN Server",
                        color = GraySecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // HEADER Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp), // Extra top padding for notch
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(media.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Official Teaser • ${media.quality}", color = PrimaryAccent, fontSize = 12.sp)
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.background(CardSlateBackground, CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Trailer", tint = Color.White)
                }
            }

            // SUBTITLE OVERLAY
            if (isPlaying && currentSeconds in 5..110) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 140.dp, start = 16.dp, end = 16.dp)
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = when (currentSeconds % 12) {
                            in 0..3 -> "In a world of absolute silence..."
                            in 4..7 -> "One hero stands against the darkness."
                            else -> "Prepare for the ultimate cinematic experience!"
                        },
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // CONTROLS AT BOTTOM
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // Time indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val min = currentSeconds / 60
                    val sec = currentSeconds % 60
                    Text(
                        text = String.format("%d:%02d", min, sec),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "-${String.format("%d:%02d", (totalSeconds - currentSeconds) / 60, (totalSeconds - currentSeconds) % 60)}",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Slider(
                    value = currentSeconds.toFloat(),
                    onValueChange = { currentSeconds = it.toLong() },
                    valueRange = 0f..totalSeconds.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = RosePremium,
                        activeTrackColor = RosePremium,
                        inactiveTrackColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Play/Pause Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(56.dp)
                            .background(RosePremium, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
