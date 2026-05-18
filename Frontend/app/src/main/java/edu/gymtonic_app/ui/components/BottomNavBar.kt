package edu.gymtonic_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.theme.LocalColors

enum class BottomNavItem {
    TRAINING,
    GROUPS,
    FRIENDS,
    CHALLENGES,
    PROFILE
}

@Composable
fun BottomNavBar(
    selectedItem: BottomNavItem,
    onOpenTraining: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
        color = colors.surfaceBottomNav,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomItem(strings.navTraining, Icons.Outlined.FitnessCenter, selectedItem == BottomNavItem.TRAINING, colors.navSelectedTint, colors.navUnselectedTint, colors.surfaceAccent, onOpenTraining, Modifier.weight(1f))
            BottomItem(strings.groupsTitle, Icons.Outlined.Group, selectedItem == BottomNavItem.GROUPS, colors.navSelectedTint, colors.navUnselectedTint, colors.surfaceAccent, onOpenGroups, Modifier.weight(1f))
            BottomItem(strings.friendsTitle, Icons.Outlined.People, selectedItem == BottomNavItem.FRIENDS, colors.navSelectedTint, colors.navUnselectedTint, colors.surfaceAccent, onOpenFriends, Modifier.weight(1f))
            BottomItem(strings.navChallenges, Icons.Outlined.EmojiEvents, selectedItem == BottomNavItem.CHALLENGES, colors.navSelectedTint, colors.navUnselectedTint, colors.surfaceAccent, onOpenChallenges, Modifier.weight(1f))
            BottomItem(strings.navProfile, Icons.Outlined.Person, selectedItem == BottomNavItem.PROFILE, colors.navSelectedTint, colors.navUnselectedTint, colors.surfaceAccent, onOpenProfile, Modifier.weight(1f))
        }
    }
}

@Composable
private fun BottomItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    selectedTint: Color,
    unselectedTint: Color,
    selectedContainer: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) selectedContainer else Color.Transparent
    val tint = if (selected) selectedTint else unselectedTint

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .padding(horizontal = 6.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = tint,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}
