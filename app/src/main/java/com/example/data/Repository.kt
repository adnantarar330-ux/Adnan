package com.example.data

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Repository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val userDao = db.userDao()
    private val profileDao = db.profileDao()
    private val mediaDao = db.mediaDao()
    private val episodeDao = db.episodeDao()
    private val watchlistDao = db.watchlistDao()
    private val downloadDao = db.downloadDao()
    private val historyDao = db.historyDao()
    private val reviewDao = db.reviewDao()

    // Active States
    var currentProfile = mutableStateOf<Profile?>(null)
    var currentUser = mutableStateOf<UserAccount?>(null)

    // Flow Exposures
    val allMedia: Flow<List<MediaContent>> = mediaDao.getAllContents()
    val featuredMedia: Flow<List<MediaContent>> = mediaDao.getFeaturedContents()
    val profiles: Flow<List<Profile>> = profileDao.getAllProfiles()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            preloadDefaults()
        }
    }

    private suspend fun preloadDefaults() {
        // Preload profiles if empty
        val existingProfiles = profileDao.getAllProfiles().firstOrNull()
        if (existingProfiles.isNullOrEmpty()) {
            profileDao.insertProfile(Profile(name = "Adult Profile", avatarUrl = "avatar_adult", type = "Adult"))
            profileDao.insertProfile(Profile(name = "Kids Profile", avatarUrl = "avatar_kids", type = "Kids"))
            profileDao.insertProfile(Profile(name = "Guest User", avatarUrl = "avatar_guest", type = "Guest"))
        }

        // Preload initial media content if empty
        val existingMedia = mediaDao.getAllContents().firstOrNull()
        if (existingMedia.isNullOrEmpty()) {
            val mediaList = listOf(
                MediaContent(
                    title = "Interstellar",
                    description = "When Earth becomes uninhabitable, a team of explorers undertakes the most important mission in human history: traveling beyond this galaxy to discover whether mankind has a future among the stars.",
                    poster = "interstellar_poster",
                    banner = "interstellar_banner",
                    trailerUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    type = "Movie",
                    rating = 4.9f,
                    quality = "4K",
                    genre = "Sci-Fi, Adventure, Drama",
                    releaseYear = 2014,
                    duration = "2h 49m",
                    director = "Christopher Nolan",
                    cast = "Matthew McConaughey, Anne Hathaway, Jessica Chastain, Michael Caine",
                    language = "English (Dolby Atmos)",
                    subAvailable = true,
                    subtitleSrt = "1\n00:00:01,000 --> 00:00:05,000\n[Atmospheric music playing]\n\n2\n00:00:06,000 --> 00:00:10,000\nCooper: We used to look up at the sky and wonder at our place in the stars.",
                    isPremium = true,
                    isFeatured = true
                ),
                MediaContent(
                    title = "The Dark Knight",
                    description = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                    poster = "dark_knight_poster",
                    banner = "dark_knight_banner",
                    trailerUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    type = "Movie",
                    rating = 4.8f,
                    quality = "4K",
                    genre = "Action, Crime, Drama",
                    releaseYear = 2008,
                    duration = "2h 32m",
                    director = "Christopher Nolan",
                    cast = "Christian Bale, Heath Ledger, Aaron Eckhart, Maggie Gyllenhaal",
                    language = "English",
                    subAvailable = true,
                    isPremium = true,
                    isFeatured = false
                ),
                MediaContent(
                    title = "Inception",
                    description = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project.",
                    poster = "inception_poster",
                    banner = "inception_banner",
                    trailerUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    type = "Movie",
                    rating = 4.8f,
                    quality = "1080p",
                    genre = "Sci-Fi, Action, Thriller",
                    releaseYear = 2010,
                    duration = "2h 28m",
                    director = "Christopher Nolan",
                    cast = "Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page, Tom Hardy",
                    language = "English",
                    subAvailable = true,
                    isPremium = false,
                    isFeatured = false
                ),
                MediaContent(
                    title = "Dune: Part Two",
                    description = "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family. Facing a choice between the love of his life and the fate of the universe, he endeavors to prevent a terrible future.",
                    poster = "dune_poster",
                    banner = "dune_banner",
                    trailerUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                    type = "Movie",
                    rating = 4.9f,
                    quality = "4K",
                    genre = "Sci-Fi, Adventure, Action",
                    releaseYear = 2024,
                    duration = "2h 46m",
                    director = "Denis Villeneuve",
                    cast = "Timothée Chalamet, Zendaya, Rebecca Ferguson, Javier Bardem",
                    language = "English (Dolby Atmos)",
                    subAvailable = true,
                    isPremium = true,
                    isFeatured = true
                ),
                MediaContent(
                    title = "Stranger Things",
                    description = "When a young boy vanishes, a small town uncovers a mystery involving secret experiments, terrifying supernatural forces and one strange little girl.",
                    poster = "stranger_things_poster",
                    banner = "stranger_things_banner",
                    trailerUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                    type = "Series",
                    rating = 4.8f,
                    quality = "4K",
                    genre = "Drama, Fantasy, Horror",
                    releaseYear = 2016,
                    duration = "4 Seasons",
                    director = "The Duffer Brothers",
                    cast = "Millie Bobby Brown, Finn Wolfhard, Winona Ryder, David Harbour",
                    language = "English",
                    subAvailable = true,
                    isPremium = true,
                    isFeatured = true,
                    seasonsCount = 4
                ),
                MediaContent(
                    title = "Loki",
                    description = "The mercurial villain Loki resumes his role as the God of Mischief in a new series that takes place after the events of “Avengers: Endgame.”",
                    poster = "loki_poster",
                    banner = "loki_banner",
                    trailerUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
                    type = "Series",
                    rating = 4.7f,
                    quality = "4K",
                    genre = "Action, Sci-Fi, Adventure",
                    releaseYear = 2021,
                    duration = "2 Seasons",
                    director = "Michael Waldron",
                    cast = "Tom Hiddleston, Owen Wilson, Sophia Di Martino, Gugu Mbatha-Raw",
                    language = "English",
                    subAvailable = true,
                    isPremium = true,
                    isFeatured = false,
                    seasonsCount = 2
                ),
                MediaContent(
                    title = "Demon Slayer",
                    description = "A family is attacked by demons and only two members survive - Tanjiro and his sister Nezuko, who is turning into a demon slowly. Tanjiro sets out to become a demon slayer to avenge his family and cure his sister.",
                    poster = "demon_slayer_poster",
                    banner = "demon_slayer_banner",
                    trailerUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
                    type = "Anime",
                    rating = 4.9f,
                    quality = "1080p",
                    genre = "Anime, Action, Fantasy",
                    releaseYear = 2019,
                    duration = "4 Seasons",
                    director = "Haruo Sotozaki",
                    cast = "Natsuki Hanae, Akari Kito, Yoshitsugu Matsuoka, Hiro Shimono",
                    language = "Japanese (Sub/Dub)",
                    subAvailable = true,
                    isPremium = true,
                    isFeatured = true,
                    seasonsCount = 4,
                    isOngoing = true,
                    isCompleted = false
                ),
                MediaContent(
                    title = "Jujutsu Kaisen",
                    description = "A boy swallows a cursed talisman - the finger of a demon - and becomes cursed himself. He enters a shaman's school to be able to locate the demon's other body parts and thus exorcise himself.",
                    poster = "jjk_poster",
                    banner = "jjk_banner",
                    trailerUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutback.mp4",
                    type = "Anime",
                    rating = 4.8f,
                    quality = "1080p",
                    genre = "Anime, Action, Supernatural",
                    releaseYear = 2020,
                    duration = "2 Seasons",
                    director = "Sunghoo Park",
                    cast = "Junya Enoki, Yuma Uchida, Asami Seto, Yuichi Nakamura",
                    language = "Japanese (Sub/Dub)",
                    subAvailable = true,
                    isPremium = false,
                    isFeatured = false,
                    seasonsCount = 2,
                    isOngoing = false,
                    isCompleted = true
                )
            )

            mediaList.forEach { content ->
                val id = mediaDao.insertContent(content).toInt()
                if (content.type == "Series" || content.type == "Anime") {
                    // Preload episodes
                    for (season in 1..(if (content.seasonsCount > 0) content.seasonsCount else 1)) {
                        for (ep in 1..4) {
                            episodeDao.insertEpisode(
                                Episode(
                                    seriesId = id,
                                    title = "S${season} Ep${ep}: The Awakening Journey",
                                    description = "As tensions rise and hidden paths are revealed, our heroes must confront an unexpected challenge that tests their inner resolve.",
                                    thumbnail = "ep_thumb_${ep}",
                                    duration = "45m",
                                    episodeNumber = ep,
                                    seasonNumber = season
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    // --- Profile CRUD ---
    suspend fun addProfile(name: String, avatar: String, type: String) {
        profileDao.insertProfile(Profile(name = name, avatarUrl = avatar, type = type))
    }

    suspend fun removeProfile(profileId: Int) {
        profileDao.deleteProfile(profileId)
    }

    // --- Auth CRUD ---
    suspend fun login(emailOrUsername: String, pass: String): Boolean {
        val account = if (emailOrUsername.contains("@")) {
            userDao.getAccountByEmail(emailOrUsername)
        } else {
            userDao.getAccountByUsername(emailOrUsername)
        }
        if (account != null && account.password == pass) {
            currentUser.value = account
            return true
        }
        return false
    }

    suspend fun signUp(email: String, phone: String, name: String, user: String, pass: String, refCode: String): Boolean {
        val exists = userDao.getAccountByEmail(email) ?: userDao.getAccountByUsername(user)
        if (exists != null) return false
        val account = UserAccount(email = email, phone = phone, name = name, username = user, password = pass, referralCode = refCode)
        val id = userDao.insertAccount(account)
        currentUser.value = account.copy(id = id.toInt())
        return true
    }

    suspend fun resetPassword(email: String, newPass: String): Boolean {
        val account = userDao.getAccountByEmail(email) ?: return false
        val updated = account.copy(password = newPass)
        userDao.updateAccount(updated)
        if (currentUser.value?.email == email) {
            currentUser.value = updated
        }
        return true
    }

    suspend fun upgradePremium() {
        val current = currentUser.value ?: return
        val updated = current.copy(isPremium = true)
        userDao.updateAccount(updated)
        currentUser.value = updated
    }

    // --- Media CRUD ---
    suspend fun addContent(content: MediaContent) {
        mediaDao.insertContent(content)
    }

    suspend fun deleteContent(content: MediaContent) {
        mediaDao.deleteContent(content)
    }

    suspend fun getContentById(id: Int): MediaContent? {
        return mediaDao.getContentById(id)
    }

    fun getEpisodesForSeries(seriesId: Int): Flow<List<Episode>> = episodeDao.getEpisodesBySeries(seriesId)

    // --- Watchlist ---
    fun getWatchlist(profileId: Int): Flow<List<WatchlistItem>> = watchlistDao.getWatchlistForProfile(profileId)

    suspend fun toggleWatchlist(mediaId: Int, profileId: Int) {
        if (watchlistDao.isInWatchlist(mediaId, profileId)) {
            watchlistDao.removeFromWatchlist(mediaId, profileId)
        } else {
            watchlistDao.addToWatchlist(WatchlistItem(mediaId = mediaId, profileId = profileId))
        }
    }

    suspend fun isMediaInWatchlist(mediaId: Int, profileId: Int): Boolean {
        return watchlistDao.isInWatchlist(mediaId, profileId)
    }

    // --- Downloads with Simulation ---
    fun getDownloads(profileId: Int): Flow<List<DownloadItem>> = downloadDao.getDownloadsForProfile(profileId)

    suspend fun startDownload(mediaId: Int, episodeId: Int?, profileId: Int, title: String, quality: String) {
        val size = when(quality) {
            "4K" -> 2400
            "1080p" -> 1100
            "720p" -> 650
            "480p" -> 350
            else -> 200
        }
        val item = DownloadItem(
            mediaId = mediaId,
            episodeId = episodeId,
            profileId = profileId,
            title = title,
            quality = quality,
            totalSizeMb = size,
            progress = 0,
            isCompleted = false
        )
        val insertedId = downloadDao.insertDownload(item).toInt()

        // Simulate download progress asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            var currentProgress = 0
            while (currentProgress < 100) {
                delay(300)
                currentProgress += (5..15).random()
                if (currentProgress > 100) currentProgress = 100
                downloadDao.insertDownload(item.copy(id = insertedId, progress = currentProgress, isCompleted = currentProgress == 100))
            }
        }
    }

    suspend fun deleteDownload(id: Int) {
        downloadDao.deleteDownload(id)
    }

    // --- Watch History ---
    fun getHistory(profileId: Int): Flow<List<WatchHistoryItem>> = historyDao.getHistoryForProfile(profileId)

    suspend fun saveHistory(mediaId: Int, episodeId: Int?, profileId: Int, progressSec: Long, totalSec: Long) {
        historyDao.insertHistoryItem(
            WatchHistoryItem(
                mediaId = mediaId,
                episodeId = episodeId,
                profileId = profileId,
                progressSeconds = progressSec,
                totalSeconds = totalSec
            )
        )
    }

    // --- Reviews ---
    fun getReviews(mediaId: Int): Flow<List<Review>> = reviewDao.getReviewsForMedia(mediaId)

    suspend fun addReview(mediaId: Int, userName: String, rating: Int, comment: String) {
        reviewDao.insertReview(Review(mediaId = mediaId, userName = userName, rating = rating, comment = comment))
    }
}
