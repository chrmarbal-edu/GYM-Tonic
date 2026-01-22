package edu.gymtonic_app.ui.components.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorialView(viewModel: EditorialViewModel, navHostController: NavHostController) {

    //Varaibles
    val listaEditoriales: List<Editorial> by viewModel.editorialesVisibles.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current


    // Estado del pull-to-refresh.
    val refreshState = rememberPullToRefreshState()

    //Que cargue automatico al iniciar
    LaunchedEffect(listaEditoriales) {
        if (listaEditoriales.isEmpty() && !loading && error == null) {
            viewModel.traerEditoriales()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar({
                Text(stringResource(R.string.app_name))
            })
        },
        modifier = Modifier.fillMaxSize()

    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (error != null) {
                Text(text = "Error: $error", color = Color.Red, modifier = Modifier.padding(16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    onClick = { viewModel.traerEditoriales() }) {
                    Text("Actualizar")
                }
            } else {
                // Implementación de Pull to Refresh.
                PullToRefreshBox(
                    isRefreshing = loading, // Usa el estado de carga del ViewModel.
                    state = refreshState, // Estado del pull-to-refresh.
                    modifier = Modifier.fillMaxSize(),
                    onRefresh = { viewModel.traerEditoriales() } // Acción al refrescar.
                ) {
                    // Contenido que se puede refrescar
                    LazyColumn {
                        items(listaEditoriales) { editorial ->
                            MyCard(
                                editorial,context,
                                onFavouritesClick = {
                                    viewModel.BaseEditorialFavorita(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}