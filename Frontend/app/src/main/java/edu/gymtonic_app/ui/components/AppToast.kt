package edu.gymtonic_app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.gymtonic_app.ui.theme.LocalColors
import kotlinx.coroutines.launch

val LocalAppSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("LocalAppSnackbarHostState no disponible. Envuelve el contenido con AppToastHost.")
}

@Composable
fun AppToastHost(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    CompositionLocalProvider(LocalAppSnackbarHostState provides snackbarHostState) {
        Scaffold(
            modifier = modifier,
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                content()
            }
        }
    }
}

@Composable
fun ObserveToastMessage(
    message: String?,
    onConsumed: () -> Unit = {}
) {
    val snackbarHostState = LocalAppSnackbarHostState.current
    LaunchedEffect(message) {
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            onConsumed()
        }
    }
}

fun showAppToast(
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope,
    message: String
) {
    scope.launch {
        snackbarHostState.showSnackbar(message)
    }
}

@Composable
fun ToastErrorRetryContent(
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalColors.current
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onRetry) {
            Text(text = retryLabel, color = colors.textPrimary)
        }
    }
}
