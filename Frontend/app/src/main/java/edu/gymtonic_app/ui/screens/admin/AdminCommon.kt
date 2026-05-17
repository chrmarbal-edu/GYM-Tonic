package edu.gymtonic_app.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.theme.LocalColors

@Composable
fun AdminListContent(
    isLoading: Boolean,
    error: String?,
    emptyMessage: String,
    itemsCount: Int,
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    val strings = LocalStrings.current
    when {
        isLoading && itemsCount == 0 -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        error != null && itemsCount == 0 -> {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(error, color = LocalColors.current.textSecondary)
                TextButton(onClick = onRetry) { Text(strings.adminRetry) }
            }
        }
        itemsCount == 0 -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(emptyMessage, color = LocalColors.current.textSecondary)
            }
        }
        else -> content()
    }
}

@Composable
fun AdminListItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    val colors = LocalColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceCard
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = colors.textPrimary, fontSize = 16.sp)
            if (!subtitle.isNullOrBlank()) {
                Text(subtitle, color = colors.textSecondary, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun <T> AdminSimpleList(
    items: List<T>,
    titleFor: (T) -> String,
    subtitleFor: (T) -> String? = { null },
    onItemClick: (T) -> Unit
) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(items) { item ->
            AdminListItem(
                title = titleFor(item),
                subtitle = subtitleFor(item),
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
fun AdminField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        singleLine = singleLine
    )
}

@Composable
fun AdminDeleteDialog(
    visible: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val strings = LocalStrings.current
    if (!visible) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(strings.adminDeleteConfirm) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.adminCancel) }
        }
    )
}

@Composable
fun AdminSaveButton(
    text: String,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(4.dp))
        } else {
            Text(text)
        }
    }
}

@Composable
fun AdminSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        placeholder = { Text(strings.adminSearchHint, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = colors.textSecondary) },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surfaceCard.copy(alpha = 0.5f),
            unfocusedContainerColor = colors.surfaceCard.copy(alpha = 0.5f),
            focusedBorderColor = colors.accent,
            unfocusedBorderColor = colors.fieldIndicator.copy(alpha = 0.3f)
        )
    )
}

@Composable
fun AdminExerciseListItem(
    title: String,
    subtitle: String,
    imageUrl: String?,
    onClick: () -> Unit
) {
    val colors = LocalColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceCard
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceMain
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    fontSize = 16.sp
                )
                Text(
                    subtitle,
                    color = colors.textSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminIntDropdown(
    label: String,
    options: List<Pair<Int, String>>,
    selectedValue: Int,
    onSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selectedValue }?.second
        ?: options.firstOrNull()?.second.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (value, text) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AdminTypeFilter(
    selectedType: Int?,
    onTypeSelected: (Int?) -> Unit,
    options: List<Pair<Int, String>>
) {
    val strings = LocalStrings.current
    
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedType == null,
                onClick = { onTypeSelected(null) },
                label = { Text(strings.adminAll) },
                shape = RoundedCornerShape(12.dp)
            )
        }
        items(options) { (id, label) ->
            FilterChip(
                selected = selectedType == id,
                onClick = { onTypeSelected(id) },
                label = { Text(label) },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun AdminDetailRow(label: String, value: String) {
    val colors = LocalColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(label, color = colors.textSecondary, fontSize = 14.sp)
        Text(
            value,
            color = colors.textPrimary,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
