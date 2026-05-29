package edu.gymtonic_app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import edu.gymtonic_app.core.MediaUtils
import edu.gymtonic_app.ui.theme.LocalColors

@Composable
fun SelectableExerciseRow(
    name: String?,
    image: String?,
    selected: Boolean,
    isFavorite: Boolean,
    onToggle: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val colors = LocalColors.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceCard,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceMain
            ) {
                AsyncImage(
                    model = MediaUtils.resolveBackendMediaUrl(image),
                    contentDescription = name ?: "Ejercicio",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(12.dp))

            // Text Content (Title + Favorite Icon)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = name ?: "Sin nombre",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = colors.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Color(0xFFE91E63) else colors.textSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            // Action Button
            TextButton(
                onClick = onToggle,
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (selected) colors.accent else Color(0xFF9EA3AF),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (selected) "Añadido" else "Añadir",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) colors.accent else Color(0xFF9EA3AF)
                )
            }
        }
    }
}
