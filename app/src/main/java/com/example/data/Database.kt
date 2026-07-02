package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ==========================================
// 1. Entities
// ==========================================

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val email: String,
    val phone: String,
    val name: String,
    val username: String,
    val password: String,
    val referralCode: String = "",
    val isPremium: Boolean = false,
    val isRemembered: Boolean = false
)

@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val avatarUrl: String, // e.g. "avatar_1", "avatar_kids", "avatar_guest"
    val type: String // "Adult", "Kids", "Guest"
)

@Entity(tableName = "media_contents")
data class MediaContent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val poster: String, // String representation or placeholder
    val banner: String, // Big backdrop poster
    val trailerUrl: String,
    val videoUrl: String,
    val type: String, // "Movie", "Series", "Anime"
    val rating: Float, // e.g. 4.8f
    val quality: String, // "4K", "1080p", "720p"
    val genre: String, // e.g. "Sci-Fi, Action"
    val releaseYear: Int,
    val duration: String, // e.g. "2h 49m" or "12 Episodes"
    val director: String,
    val cast: String, // comma-separated names
    val language: String, // e.g. "English"
    val subAvailable: Boolean,
    val subtitleSrt: String = "",
    val isPremium: Boolean = false,
    val isOngoing: Boolean = false,
    val isCompleted: Boolean = true,
    val isFeatured: Boolean = false,
    val seasonsCount: Int = 0,
    val subtitleLanguages: String = "English, Spanish, French",
    val videoResolutions: String = "360p, 480p, 720p, 1080p, 4K"
)

@Entity(tableName = "episodes")
data class Episode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val seriesId: Int,
    val title: String,
    val description: String,
    val thumbnail: String,
    val duration: String, // e.g. "45m"
    val episodeNumber: Int,
    val seasonNumber: Int,
    val watchProgress: Long = 0, // last playback offset
    val durationTotal: Long = 2700 // total seconds, e.g. 45 min
)

@Entity(tableName = "watchlist_items")
data class WatchlistItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mediaId: Int,
    val profileId: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "download_items")
data class DownloadItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mediaId: Int,
    val episodeId: Int? = null,
    val profileId: Int,
    val title: String,
    val quality: String, // e.g. "1080p"
    val totalSizeMb: Int,
    val progress: Int, // 0 to 100
    val isCompleted: Boolean,
    val filePath: String = ""
)

@Entity(tableName = "watch_history_items")
data class WatchHistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mediaId: Int,
    val episodeId: Int? = null,
    val profileId: Int,
    val watchedAt: Long = System.currentTimeMillis(),
    val progressSeconds: Long,
    val totalSeconds: Long
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mediaId: Int,
    val userName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)

// ==========================================
// 2. DAOs
// ==========================================

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun getAccountByEmail(email: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE username = :username LIMIT 1")
    suspend fun getAccountByUsername(username: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE isRemembered = 1 LIMIT 1")
    suspend fun getRememberedAccount(): UserAccount?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: UserAccount): Long

    @Update
    suspend fun updateAccount(account: UserAccount)
}

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles")
    fun getAllProfiles(): Flow<List<Profile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: Profile): Long

    @Query("DELETE FROM profiles WHERE id = :profileId")
    suspend fun deleteProfile(profileId: Int)
}

@Dao
interface MediaContentDao {
    @Query("SELECT * FROM media_contents ORDER BY id DESC")
    fun getAllContents(): Flow<List<MediaContent>>

    @Query("SELECT * FROM media_contents WHERE type = :type")
    fun getContentsByType(type: String): Flow<List<MediaContent>>

    @Query("SELECT * FROM media_contents WHERE isFeatured = 1")
    fun getFeaturedContents(): Flow<List<MediaContent>>

    @Query("SELECT * FROM media_contents WHERE id = :id LIMIT 1")
    suspend fun getContentById(id: Int): MediaContent?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: MediaContent): Long

    @Delete
    suspend fun deleteContent(content: MediaContent)
}

@Dao
interface EpisodeDao {
    @Query("SELECT * FROM episodes WHERE seriesId = :seriesId ORDER BY seasonNumber, episodeNumber")
    fun getEpisodesBySeries(seriesId: Int): Flow<List<Episode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: Episode): Long

    @Query("UPDATE episodes SET watchProgress = :progress WHERE id = :episodeId")
    suspend fun updateEpisodeProgress(episodeId: Int, progress: Long)
}

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist_items WHERE profileId = :profileId")
    fun getWatchlistForProfile(profileId: Int): Flow<List<WatchlistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchlist(item: WatchlistItem)

    @Query("DELETE FROM watchlist_items WHERE mediaId = :mediaId AND profileId = :profileId")
    suspend fun removeFromWatchlist(mediaId: Int, profileId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist_items WHERE mediaId = :mediaId AND profileId = :profileId)")
    suspend fun isInWatchlist(mediaId: Int, profileId: Int): Boolean
}

@Dao
interface DownloadDao {
    @Query("SELECT * FROM download_items WHERE profileId = :profileId")
    fun getDownloadsForProfile(profileId: Int): Flow<List<DownloadItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(item: DownloadItem): Long

    @Query("DELETE FROM download_items WHERE id = :id")
    suspend fun deleteDownload(id: Int)
}

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history_items WHERE profileId = :profileId ORDER BY watchedAt DESC")
    fun getHistoryForProfile(profileId: Int): Flow<List<WatchHistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryItem(item: WatchHistoryItem)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE mediaId = :mediaId ORDER BY timestamp DESC")
    fun getReviewsForMedia(mediaId: Int): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)
}

// ==========================================
// 3. Database
// ==========================================

@Database(
    entities = [
        UserAccount::class,
        Profile::class,
        MediaContent::class,
        Episode::class,
        WatchlistItem::class,
        DownloadItem::class,
        WatchHistoryItem::class,
        Review::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserAccountDao
    abstract fun profileDao(): ProfileDao
    abstract fun mediaDao(): MediaContentDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun downloadDao(): DownloadDao
    abstract fun historyDao(): WatchHistoryDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cinestream_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
