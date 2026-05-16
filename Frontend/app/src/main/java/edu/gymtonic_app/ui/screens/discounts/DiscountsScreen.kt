package edu.gymtonic_app.ui.screens.discounts

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import kotlin.random.Random
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.components.BottomNavBar
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.components.LanguageButton
import edu.gymtonic_app.ui.components.ThemeButton
import edu.gymtonic_app.ui.i18n.AppStrings
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.DiscountsUiState
import edu.gymtonic_app.ui.viewmodel.DiscountsViewModel

private data class DiscountTier(
    val percent: Int,
    val requiredPoints: Int
)

private val discountTiers = listOf(
    DiscountTier(percent = 5, requiredPoints = 150),
    DiscountTier(percent = 10, requiredPoints = 400),
    DiscountTier(percent = 15, requiredPoints = 800),
    DiscountTier(percent = 20, requiredPoints = 1300)
)

@Composable
fun DiscountsScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: DiscountsViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val bg = Brush.verticalGradient(colors.gradientColors)
    val uiState by viewModel.uiState.collectAsState()

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
                HeaderRow(
                    title = strings.discountsTitle,
                    onBack = onBack,
                    backLabel = strings.back
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    when (val state = uiState) {
                        is DiscountsUiState.Loading -> LoadingContent(label = strings.discountsLoading)
                        is DiscountsUiState.Error -> ErrorContent(
                            message = strings.discountsErrorGeneric,
                            retryLabel = strings.discountsRetry,
                            onRetry = { viewModel.loadPoints() }
                        )
                        is DiscountsUiState.Success -> SuccessContent(
                            points = state.points,
                            strings = strings
                        )
                    }
                }

                BottomNavBar(
                    selectedItem = BottomNavItem.HOME,
                    onOpenHome = onOpenHome,
                    onOpenTraining = onOpenTraining,
                    onOpenChallenges = onOpenChallenges,
                    onOpenProfile = onOpenProfile
                )
            }
        }
    }
}

@Composable
private fun HeaderRow(title: String, onBack: () -> Unit, backLabel: String) {
    val colors = LocalColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = backLabel,
                tint = colors.fieldIndicator
            )
        }

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
private fun LoadingContent(label: String) {
    val colors = LocalColors.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(color = colors.accent)
        Spacer(Modifier.height(12.dp))
        Text(text = label, color = colors.textSecondary, fontSize = 14.sp)
    }
}

@Composable
private fun ErrorContent(message: String, retryLabel: String, onRetry: () -> Unit) {
    val colors = LocalColors.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = colors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.accent,
                contentColor = Color.White
            )
        ) {
            Text(text = retryLabel)
        }
    }
}

@Composable
private fun SuccessContent(
    points: Int,
    strings: AppStrings
) {
    val colors = LocalColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = colors.surfaceAccent,
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFF4C542), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Stars,
                        contentDescription = null,
                        tint = Color(0xFF1D1D1D),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.discountsYourPoints,
                        color = colors.textOnAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = points.toString(),
                            color = colors.textOnAccent,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = strings.discountsPointsSuffix,
                            color = colors.textOnAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalOffer,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = strings.discountsAvailableTitle,
                color = colors.textPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        discountTiers.forEach { tier ->
            DiscountTierCard(
                points = points,
                tier = tier,
                strings = strings
            )
        }

        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun DiscountTierCard(
    points: Int,
    tier: DiscountTier,
    strings: AppStrings
) {
    val colors = LocalColors.current
    val unlocked = points >= tier.requiredPoints
    val progress = (points.toFloat() / tier.requiredPoints).coerceIn(0f, 1f)
    val remaining = (tier.requiredPoints - points).coerceAtLeast(0)

    var showDialog by remember { mutableStateOf(false) }
    val code = rememberSaveable(tier.percent) { generateDiscountCode(tier.percent) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = colors.surfaceCard,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(
                            if (unlocked) Color(0xFFF4C542) else colors.surfaceMain,
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (unlocked) Icons.Outlined.Verified else Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = if (unlocked) Color(0xFF1D1D1D) else colors.textSecondary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.discountsTierLabel(tier.percent),
                        color = colors.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = strings.discountsRequiredPoints(tier.requiredPoints),
                        color = colors.textSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = if (unlocked) strings.discountsUnlocked
                    else strings.discountsPointsToUnlock(remaining),
                    color = if (unlocked) colors.accent else colors.textSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = colors.accent,
                trackColor = colors.surfaceMain
            )

            if (unlocked) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = strings.discountsClaim,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (showDialog) {
        DiscountCodeDialog(
            code = code,
            strings = strings,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun DiscountCodeDialog(
    code: String,
    strings: AppStrings,
    onDismiss: () -> Unit
) {
    val colors = LocalColors.current
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = strings.discountsCodeTitle,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colors.surfaceMain,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = code,
                        color = colors.textPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp)
                    )
                }
                Text(
                    text = strings.discountsCodeMessage,
                    color = colors.textSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                clipboard.setText(AnnotatedString(code))
                Toast.makeText(context, strings.discountsCodeCopied, Toast.LENGTH_SHORT).show()
            }) {
                Text(text = strings.discountsCopyCode, color = colors.accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = strings.discountsClose, color = colors.textSecondary)
            }
        }
    )
}

private fun generateDiscountCode(percent: Int): String {
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    val block = (1..4).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    val block2 = (1..4).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    return "GT$percent-$block-$block2"
}
