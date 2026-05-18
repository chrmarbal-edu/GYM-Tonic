package edu.gymtonic_app.ui.screens.missions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.components.BottomNavBar
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.components.LanguageButton
import edu.gymtonic_app.ui.components.ThemeButton
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.CalendarDayUi
import edu.gymtonic_app.ui.viewmodel.CalendarDayUiStatus
import edu.gymtonic_app.ui.viewmodel.WeeklyGoalUi

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun WeekChallengesScreen(
    onBack: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
    onShowMoreCalendar: () -> Unit,
    goals: List<WeeklyGoalUi> = emptyList(),
    calendarDays: List<CalendarDayUi> = emptyList(),
    calendarYear: Int = 0,
    calendarMonth: Int = 0,
    achievedLabel: String = "0/0",
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val bg = Brush.verticalGradient(colors.gradientColors)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 42.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(34.dp),
            color = colors.surfaceMain,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp)
            ) {
                HeaderRow(title = strings.weekTitle)

                PullToRefreshBox(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp),
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh
                ) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strings.weeklyGoals,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = achievedLabel,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        items(goals) { goal ->
                            GoalCard(goal = goal)
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = strings.myCalendar,
                                    color = colors.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = strings.showMore,
                                    color = colors.accentDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .clickable { onShowMoreCalendar() }
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Transparent)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        item {
                            CalendarCard(days = calendarDays, year = calendarYear, month = calendarMonth)
                        }
                    }
                }

                BottomNavBar(
                    selectedItem = BottomNavItem.CHALLENGES,
                    onOpenTraining = onOpenTraining,
                    onOpenGroups = onOpenGroups,
                    onOpenFriends = onOpenFriends,
                    onOpenChallenges = onOpenChallenges,
                    onOpenProfile = onOpenProfile
                )
            }
        }
    }
}

@Composable
private fun HeaderRow(title: String) {
    val colors = LocalColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.size(36.dp))

        Text(
            text = title,
            color = colors.textPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            ThemeButton(tint = colors.fieldIndicator)
            LanguageButton(tint = colors.fieldIndicator)
        }
    }
}

@Composable
private fun GoalCard(goal: WeeklyGoalUi) {
    val colors = LocalColors.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.surfaceAccent,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFF4C542),
                modifier = Modifier.size(34.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.title,
                    color = colors.textOnAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = goal.progressLabel,
                    color = colors.textOnAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 1.dp, bottom = 4.dp)
                )
                LinearProgressIndicator(
                    progress = { goal.progress },
                    color = Color(0xFF1EF847),
                    trackColor = Color(0xFFE6E8EB),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            }

            Text(
                text = goal.pointsLabel,
                color = colors.textOnAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun CalendarCard(days: List<CalendarDayUi>, year: Int, month: Int) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val doneColor = Color(0xFF22FF19)
    val missedColor = Color(0xFFFF1A1A)
    val pendingColor = if (colors.isDark) Color(0xFF3A3D5A) else Color(0xFFD0D3DB)
    val todayBorderColor = colors.accentDark

    val effectiveMonth = if (month in 1..12) month else java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
    val effectiveYear = if (year > 0) year else java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    val monthName = strings.calendarMonthNames.getOrNull(effectiveMonth - 1) ?: return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceAccent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "$monthName $effectiveYear",
                color = colors.textOnAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                strings.calendarDayNames.forEach { dayName ->
                    Text(
                        text = dayName,
                        modifier = Modifier.weight(1f),
                        color = colors.textOnAccent.copy(alpha = 0.55f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            days.chunked(7).forEach { chunk ->
                val row = if (chunk.size < 7) chunk + List(7 - chunk.size) {
                    CalendarDayUi(dayIndex = -1, dayNumber = 0, status = CalendarDayUiStatus.PENDING)
                } else chunk
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    row.forEach { day ->
                        if (day.dayNumber == 0) {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else {
                            val bgColor = when (day.status) {
                                CalendarDayUiStatus.DONE -> doneColor
                                CalendarDayUiStatus.MISSED -> missedColor
                                else -> pendingColor
                            }
                            val textColor = when (day.status) {
                                CalendarDayUiStatus.DONE -> Color(0xFF0D3200)
                                CalendarDayUiStatus.MISSED -> Color.White
                                else -> colors.textOnAccent.copy(alpha = 0.75f)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .then(
                                        if (day.isToday) Modifier.border(
                                            2.dp, todayBorderColor, RoundedCornerShape(6.dp)
                                        ) else Modifier
                                    )
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(bgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.dayNumber.toString(),
                                    color = textColor,
                                    fontSize = 11.sp,
                                    fontWeight = if (day.isToday) FontWeight.ExtraBold else FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
