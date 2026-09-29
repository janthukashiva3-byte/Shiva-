package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.GameOverDialog
import com.example.ui.components.GamingBottomNavigation
import com.example.ui.components.GamingTopBar
import com.example.ui.games.GameScreenHost
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.GamesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameZoneViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                GameZoneApp()
            }
        }
    }
}

@Composable
fun GameZoneApp(
    viewModel: GameZoneViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val games by viewModel.games.collectAsStateWithLifecycle()
    val records by viewModel.recentRecords.collectAsStateWithLifecycle()
    val challenges by viewModel.dailyChallenges.collectAsStateWithLifecycle()
    val achievements by viewModel.achievements.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val leaderboardTab by viewModel.selectedLeaderboardTab.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
    val gameOverState by viewModel.gameOverState.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground)
    ) {
        when (val screen = currentScreen) {
            is Screen.Splash -> {
                SplashScreen(
                    onFinished = { viewModel.navigateTo(Screen.Home) }
                )
            }
            is Screen.Auth -> {
                AuthScreen(
                    onLoginSuccess = { email, pass -> viewModel.login(email, pass) },
                    onSignupSuccess = { username, email -> viewModel.signup(username, email) },
                    onGoogleLogin = { viewModel.loginWithGoogle() },
                    onGuestLogin = { viewModel.login("guest@gamezone.io", "guest") }
                )
            }
            is Screen.GamePlay -> {
                val stats = games.find { it.gameId == screen.gameId }
                GameScreenHost(
                    gameId = screen.gameId,
                    gameStats = stats,
                    onBack = { viewModel.navigateTo(Screen.Games) },
                    onFinishGame = { gId, title, score, prevHigh ->
                        viewModel.recordGameScore(gId, title, score, prevHigh)
                    }
                )
            }
            is Screen.Notifications -> {
                NotificationsScreen(
                    notifications = notifications,
                    onBack = { viewModel.navigateTo(Screen.Home) },
                    onMarkRead = { id -> viewModel.markNotificationRead(id) },
                    onMarkAllRead = { viewModel.markAllNotificationsRead() }
                )
            }
            is Screen.Admin -> {
                AdminScreen(
                    user = userProfile,
                    games = games,
                    onBack = { viewModel.navigateTo(Screen.Profile) },
                    onToggleGameActive = { gId, current -> viewModel.toggleGameActive(gId, current) },
                    onToggleGameFeatured = { gId, current -> viewModel.toggleGameFeatured(gId, current) },
                    onGrantPoints = { pts -> viewModel.adminGrantPoints(pts) },
                    onSendBroadcast = { title, msg -> viewModel.adminBroadcast(title, msg) }
                )
            }
            else -> {
                // Secondary screens handle BackHandler to return to Home
                if (currentScreen != Screen.Home) {
                    BackHandler { viewModel.navigateTo(Screen.Home) }
                }

                Scaffold(
                    topBar = {
                        GamingTopBar(
                            user = userProfile,
                            unreadCount = unreadCount,
                            onAvatarClick = { viewModel.navigateTo(Screen.Profile) },
                            onPointsClick = { viewModel.navigateTo(Screen.Rewards) },
                            onNotificationClick = { viewModel.navigateTo(Screen.Notifications) },
                            onAdminClick = { viewModel.navigateTo(Screen.Admin) }
                        )
                    },
                    bottomBar = {
                        GamingBottomNavigation(
                            currentScreen = currentScreen,
                            onNavigate = { dest -> viewModel.navigateTo(dest) }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = CyberBackground
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            is Screen.Home -> {
                                HomeScreen(
                                    user = userProfile,
                                    games = games,
                                    challenges = challenges,
                                    leaderboard = leaderboard,
                                    onLaunchGame = { gId -> viewModel.launchGame(gId) },
                                    onClaimStreak = { viewModel.claimDailyStreak() },
                                    onClaimChallenge = { id -> viewModel.claimChallenge(id) },
                                    onViewAllGames = { viewModel.navigateTo(Screen.Games) },
                                    onViewLeaderboard = { viewModel.navigateTo(Screen.Leaderboard) }
                                )
                            }
                            is Screen.Games -> {
                                GamesScreen(
                                    games = games,
                                    onLaunchGame = { gId -> viewModel.launchGame(gId) }
                                )
                            }
                            is Screen.Leaderboard -> {
                                LeaderboardScreen(
                                    selectedTab = leaderboardTab,
                                    onTabSelect = { tab -> viewModel.setLeaderboardTab(tab) },
                                    users = leaderboard
                                )
                            }
                            is Screen.Rewards -> {
                                RewardsScreen(
                                    user = userProfile,
                                    challenges = challenges,
                                    onClaimDailyStreak = { viewModel.claimDailyStreak() },
                                    onClaimChallenge = { id -> viewModel.claimChallenge(id) }
                                )
                            }
                            is Screen.Profile -> {
                                ProfileScreen(
                                    user = userProfile,
                                    games = games,
                                    records = records,
                                    achievements = achievements,
                                    onUpdateProfile = { name, avatar, bio, fav ->
                                        viewModel.updateProfile(name, avatar, bio, fav)
                                    },
                                    onOpenAdmin = { viewModel.navigateTo(Screen.Admin) },
                                    onLogout = { viewModel.logout() }
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        // Global Game Over Modal
        GameOverDialog(
            state = gameOverState,
            onPlayAgain = {
                val gameId = gameOverState.gameId
                viewModel.dismissGameOver()
                if (gameId.isNotEmpty()) {
                    viewModel.launchGame(gameId)
                }
            },
            onExit = {
                viewModel.dismissGameOver()
                viewModel.navigateTo(Screen.Games)
            }
        )
    }
}
