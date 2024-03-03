package co.touchlab.kampkit.models

import co.touchlab.kampkit.utils.doOnError
import co.touchlab.kampkit.utils.doOnSuccess
import co.touchlab.skie.configuration.annotations.DefaultArgumentInterop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PictureViewModel(
    private val breed: String,
    private val pictureRepository: PictureRepository,
) : ViewModel() {

    private val _state: MutableStateFlow<PictureViewState> = MutableStateFlow(PictureViewState.Loading)
    val state: StateFlow<PictureViewState> = _state.asStateFlow()

    suspend fun activate() {
        pictureRepository.getDogPicture(breed)
            .doOnSuccess { _state.tryEmit(PictureViewState.Content(it.message)) }
            .doOnError { _state.tryEmit(PictureViewState.Error) }
    }
}

sealed interface PictureViewState {
    data object Loading : PictureViewState
    data object Error : PictureViewState
    data class Content @DefaultArgumentInterop.Enabled constructor(
        val pictureUrl: String,
    ) : PictureViewState
}
