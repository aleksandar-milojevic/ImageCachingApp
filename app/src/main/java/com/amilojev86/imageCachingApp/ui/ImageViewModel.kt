package com.amilojev86.imageCachingApp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amilojev86.imageCachingApp.data.ImageItem
import com.amilojev86.imageCachingApp.data.ImageRepository
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val items: List<ImageItem>) : UiState()
    data class Error(val message: String) : UiState()
}

class ImageViewModel(
    private val repository: ImageRepository = ImageRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<UiState>(UiState.Loading)
    val uiState: LiveData<UiState> = _uiState

    init {
        loadImages()
    }

    fun loadImages() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            repository.fetchImages()
                .onSuccess { _uiState.value = UiState.Success(it) }
                .onFailure { _uiState.value = UiState.Error(it.message ?: "Unknown error") }
        }
    }
}
