package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameZoneDatabase
import com.example.data.GameZoneRepository
import com.example.data.model.AchievementEntity
import com.example.data.model.DailyChallengeEntity
import com.example.data.model.GameRecordEntity
import com.example.data.model.GameStatsEntity
import com.example.data.model.LeaderboardUser
import com.example.data.model.NotificationEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Games : Screen("games")
    object Leaderboard : Screen("leaderboard")
    object Rewards : Screen("rewards")
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")
    object Admin : Screen("admin")
    data class GamePlay(val gameId: String) : Screen("game_play/$gameId")
}

data class GameOverDialogState(
    val isVisible: Boolean = false,
    val gameId: String = "",
    val gameTitle: String = "",
    val finalScore: Int = 0,
    val pointsEarned: Int = 0,
    val isNewHighScore: Boolean = false
)

class GameZoneViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameZoneRepository

    init {
        val db = GameZoneDatabase.getInstance(application)
        repository = GameZoneRepository(db)
    }

    // Navigation & Screen State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Auth State
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Data streams from repository
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val games: StateFlow<List<GameStatsEntity>> = repository.allGames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentRecords: StateFlow<List<GameRecordEntity>> = repository.recentRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyChallenges: StateFlow<List<DailyChallengeEntity>> = repository.dailyChallenges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<AchievementEntity>> = repository.achievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Leaderboard
    private val _selectedLeaderboardTab = MutableStateFlow("global")
    val selectedLeaderboardTab: StateFlow<String> = _selectedLeaderboardTab.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val leaderboard: StateFlow<List<LeaderboardUser>> = _selectedLeaderboardTab
        .flatMapLatest { tab -> repository.getLeaderboard(tab) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Game Over Dialog State
    private val _gameOverState = MutableStateFlow(GameOverDialogState())
    val gameOverState: StateFlow<GameOverDialogState> = _gameOverState.asStateFlow()

    // Status snackbar / toast message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun setLeaderboardTab(tab: String) {
        _selectedLeaderboardTab.value = tab
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun launchGame(gameId: String) {
        _currentScreen.value = Screen.GamePlay(gameId)
    }

    fun recordGameScore(gameId: String, gameTitle: String, score: Int, previousHighScore: Int) {
        viewModelScope.launch {
            val pointsEarned = repository.saveGameResult(gameId, score)
            val isNewHighScore = score > previousHighScore
            _gameOverState.value = GameOverDialogState(
                isVisible = true,
                gameId = gameId,
                gameTitle = gameTitle,
                finalScore = score,
                pointsEarned = pointsEarned,
                isNewHighScore = isNewHighScore
            )
        }
    }

    fun dismissGameOver() {
        _gameOverState.value = GameOverDialogState()
    }

    fun claimDailyStreak() {
        viewModelScope.launch {
            val points = repository.claimDailyStreakReward()
            if (points > 0) {
                showMessage("🎉 Claimed +$points PTS for your daily login streak!")
            }
        }
    }

    fun claimChallenge(challengeId: Int) {
        viewModelScope.launch {
            val success = repository.claimChallenge(challengeId)
            if (success) {
                showMessage("✨ Challenge claimed! Points added to balance.")
            }
        }
    }

    fun updateProfile(username: String, avatarId: Int, bio: String, favGame: String) {
        viewModelScope.launch {
            repository.updateUserProfile(username, avatarId, bio, favGame)
            showMessage("✅ Profile updated successfully!")
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showMessage("All notifications marked as read.")
        }
    }

    // Auth flows
    fun login(emailOrPhone: String, pass: String) {
        _isLoggedIn.value = true
        _currentScreen.value = Screen.Home
        showMessage("Welcome back, Gamer! 🚀")
    }

    fun loginWithGoogle() {
        _isLoggedIn.value = true
        _currentScreen.value = Screen.Home
        showMessage("Signed in with Google! 🎮")
    }

    fun signup(username: String, email: String) {
        viewModelScope.launch {
            repository.updateUserProfile(username, 0, "Ready to climb the GameZone ladder!", "Snake Neon")
            _isLoggedIn.value = true
            _currentScreen.value = Screen.Home
            showMessage("Account created! Welcome to GameZone, $username!")
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentScreen.value = Screen.Auth
        showMessage("You have logged out.")
    }

    // Admin Dashboard Actions
    fun toggleGameActive(gameId: String, currentActive: Boolean) {
        viewModelScope.launch {
            repository.toggleGameEnabled(gameId, !currentActive)
            showMessage("Game status updated.")
        }
    }

    fun toggleGameFeatured(gameId: String, currentFeatured: Boolean) {
        viewModelScope.launch {
            repository.toggleGameFeatured(gameId, !currentFeatured)
            showMessage("Game featured status updated.")
        }
    }

    fun adminGrantPoints(points: Int) {
        viewModelScope.launch {
            repository.adminAwardPoints(points)
            showMessage("Granted $points PTS to player.")
        }
    }

    fun adminBroadcast(title: String, message: String) {
        viewModelScope.launch {
            repository.sendSystemBroadcast(title, message)
            showMessage("Broadcast notification delivered.")
        }
    }
}
