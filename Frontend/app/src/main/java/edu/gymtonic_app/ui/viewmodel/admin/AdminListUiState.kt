package edu.gymtonic_app.ui.viewmodel.admin

data class AdminListUiState<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
)

data class AdminDetailUiState<T>(
    val item: T? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val deleted: Boolean = false
)
