package edu.gymtonic_app.ui.screens.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.components.BottomNavBar
import edu.gymtonic_app.ui.components.BottomNavItem

@Composable
fun TrainingShellScreen(
    title: String,
    onBack: () -> Unit,
    showBack: Boolean = true,
    showBottomBar: Boolean,
    selectedBottomItem: BottomNavItem? = null,
    onOpenHome: () -> Unit = {},
    onOpenTraining: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF1F3F73),
            Color(0xFF3A2F7A),
            Color(0xFF2A3344)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 42.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(34.dp),
            color = Color(0xFFD9D9D9),
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 24.dp)
            ) {
                TrainingShellHeader(
                    title = title,
                    onBack = onBack,
                    showBack = showBack
                )

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    content()
                }

                if (showBottomBar && selectedBottomItem != null) {
                    BottomNavBar(
                        selectedItem = selectedBottomItem,
                        onOpenHome = onOpenHome,
                        onOpenTraining = onOpenTraining,
                        onOpenChallenges = onOpenChallenges,
                        onOpenProfile = onOpenProfile
                    )
                }
            }
        }
    }
}

@Composable
private fun TrainingShellHeader(
    title: String,
    onBack: () -> Unit,
    showBack: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = "Volver",
                    tint = Color(0xFF2D2D2D)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(36.dp))
        }

        Text(
            text = title,
            color = Color(0xFF1D1D1D),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.size(40.dp))
    }
}

