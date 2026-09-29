package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.GoldColor
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val category: String
)

@Composable
fun QuizGame(
    onFinishGame: (score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val questions = remember {
        listOf(
            QuizQuestion(
                "What is the original name of Mario in Donkey Kong (1981)?",
                listOf("Jumpman", "Plumber Boy", "Red Cap", "Luigi"),
                0,
                "Retro Arcade"
            ),
            QuizQuestion(
                "Which gaming franchise features the Master Chief as the main protagonist?",
                listOf("Destiny", "Gears of War", "Halo", "Mass Effect"),
                2,
                "FPS Lore"
            ),
            QuizQuestion(
                "In Minecraft, what substance is required to craft a portal to the Nether?",
                listOf("Bedrock", "Obsidian", "Netherite", "Crying Quartz"),
                1,
                "Sandbox"
            ),
            QuizQuestion(
                "What legendary weapon does Link wield in The Legend of Zelda?",
                listOf("Buster Sword", "Master Sword", "Keyblade", "Soul Edge"),
                1,
                "Action RPG"
            ),
            QuizQuestion(
                "Which company created the PlayStation console after a partnership with Nintendo broke down?",
                listOf("Sega", "Sony", "Microsoft", "Atari"),
                1,
                "Gaming History"
            ),
            QuizQuestion(
                "In Pokémon Red & Blue, what type of Pokémon is Pikachu?",
                listOf("Electric", "Normal", "Psychic", "Fighting"),
                0,
                "RPG"
            ),
            QuizQuestion(
                "What is the name of the futuristic neon city where Cyberpunk 2077 takes place?",
                listOf("Neo Tokyo", "Raccoon City", "Night City", "Midgar"),
                2,
                "Cyberpunk"
            ),
            QuizQuestion(
                "Who is the main protagonist in God of War?",
                listOf("Zeus", "Kratos", "Ares", "Dante"),
                1,
                "Mythology"
            )
        )
    }

    var currentQIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableIntStateOf(15) }
    var usedFiftyFifty by remember { mutableStateOf(false) }
    var usedSkip by remember { mutableStateOf(false) }
    var hiddenOptions by remember { mutableStateOf(listOf<Int>()) }

    val currentQ = questions[currentQIndex]

    // Question Timer
    LaunchedEffect(currentQIndex, isAnswered) {
        if (!isAnswered) {
            timerSeconds = 15
            while (timerSeconds > 0 && !isAnswered) {
                delay(1000)
                timerSeconds--
                if (timerSeconds == 0) {
                    // Time out
                    isAnswered = true
                    selectedAnswer = -1
                    streak = 0
                }
            }
        }
    }

    fun nextQuestion() {
        if (currentQIndex < questions.size - 1) {
            currentQIndex++
            selectedAnswer = null
            isAnswered = false
            hiddenOptions = emptyList()
        } else {
            // Completed all questions
            val finalScore = score + (streak * 20)
            onFinishGame(maxOf(50, finalScore))
        }
    }

    fun handleFiftyFifty() {
        if (usedFiftyFifty || isAnswered) return
        usedFiftyFifty = true
        val wrongIndices = currentQ.options.indices.filter { it != currentQ.correctIndex }.shuffled().take(2)
        hiddenOptions = wrongIndices
    }

    fun handleSkip() {
        if (usedSkip || isAnswered) return
        usedSkip = true
        nextQuestion()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Stats & Timer
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberSurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("QUESTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text("${currentQIndex + 1} / ${questions.size}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NeonCyan)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("STREAK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text("🔥 $streak", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (streak > 2) NeonPink else GoldColor)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("SCORE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text("$score", fontSize = 20.sp, fontWeight = FontWeight.Black, color = NeonGreen)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Timer Bar
            LinearProgressIndicator(
                progress = { timerSeconds / 15f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (timerSeconds < 5) NeonPink else NeonCyan,
                trackColor = CyberSurface
            )
        }

        // Question Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CyberSurface)
                .border(2.dp, CyberCardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonPurple.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = currentQ.category.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPurple
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentQ.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Options List
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            currentQ.options.forEachIndexed { idx, opt ->
                val isHidden = hiddenOptions.contains(idx)
                if (!isHidden) {
                    val isCorrect = idx == currentQ.correctIndex
                    val isChosen = selectedAnswer == idx
                    val cardBg = when {
                        isAnswered && isCorrect -> NeonGreen.copy(alpha = 0.25f)
                        isAnswered && isChosen && !isCorrect -> NeonPink.copy(alpha = 0.25f)
                        else -> CyberSurfaceVariant
                    }
                    val borderColor = when {
                        isAnswered && isCorrect -> NeonGreen
                        isAnswered && isChosen && !isCorrect -> NeonPink
                        else -> CyberCardBorder
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isAnswered) {
                                selectedAnswer = idx
                                isAnswered = true
                                if (idx == currentQ.correctIndex) {
                                    val points = 50 + (timerSeconds * 5) + (streak * 10)
                                    score += points
                                    streak++
                                } else {
                                    streak = 0
                                }
                            }
                            .padding(horizontal = 16.dp)
                            .testTag("quiz_option_$idx"),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "${('A' + idx)}.  $opt",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isAnswered && isCorrect) NeonGreen else TextPrimary
                        )
                    }
                }
            }
        }

        // Lifelines and Next Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { handleFiftyFifty() },
                enabled = !usedFiftyFifty && !isAnswered,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("quiz_lifeline_5050"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
            ) {
                Text(
                    text = if (usedFiftyFifty) "50:50 (USED)" else "50:50",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (usedFiftyFifty) TextSecondary else NeonYellow
                )
            }

            Button(
                onClick = { handleSkip() },
                enabled = !usedSkip && !isAnswered,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("quiz_lifeline_skip"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberSurfaceVariant)
            ) {
                Text(
                    text = if (usedSkip) "SKIP (USED)" else "SKIP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (usedSkip) TextSecondary else NeonPurple
                )
            }

            if (isAnswered) {
                Button(
                    onClick = { nextQuestion() },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp)
                        .testTag("quiz_next_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("NEXT ❯", fontSize = 12.sp, fontWeight = FontWeight.Black, color = CyberBackground)
                }
            }
        }
    }
}
