package co.touchlab.kampkit.android.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kampkit.android.R
import co.touchlab.kampkit.db.Breed
import co.touchlab.kampkit.models.BreedViewModel
import co.touchlab.kampkit.models.BreedViewState
import co.touchlab.kampkit.models.PictureViewModel
import co.touchlab.kampkit.models.PictureViewState
import co.touchlab.kermit.Logger
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.LocalDateTime

@Composable
fun MainScreen(
    viewModel: BreedViewModel = koinViewModel(),
    log: Logger,
    onToDetails: (Breed, String) -> Unit,
    onBack: () -> Unit
) {
    val dogsState by viewModel.breedState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.activate()
    }
    BackHandler {
        onBack()
    }
    MainScreenContent(
        dogsState = dogsState,
        onRefresh = { scope.launch { viewModel.refreshBreeds() } },
        onSuccess = { data -> log.v { "View updating with ${data.size} breeds" } },
        onError = { exception -> log.e { "Displaying error: $exception" } },
        onFavorite = { scope.launch { viewModel.updateBreedFavorite(it) } },
        onBreedSelected = onToDetails
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreenContent(
    dogsState: BreedViewState,
    onRefresh: () -> Unit = {},
    onSuccess: (List<Breed>) -> Unit = {},
    onError: (String) -> Unit = {},
    onFavorite: (Breed) -> Unit = {},
    onBreedSelected: (Breed, String) -> Unit,
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        val refreshState = rememberPullRefreshState(dogsState.isLoading, onRefresh)

        Box(Modifier.pullRefresh(refreshState)) {
            when (dogsState) {
                is BreedViewState.Empty -> Empty()
                is BreedViewState.Content -> {
                    val breeds = dogsState.breeds
                    onSuccess(breeds)
                    Success(
                        successData = breeds,
                        favoriteBreed = onFavorite,
                        drillDown = onBreedSelected,
                    )
                }

                is BreedViewState.Error -> {
                    val error = dogsState.error
                    onError(error)
                    Error(error)
                }

                BreedViewState.Initial -> {
                    // no-op (just show spinner until first data is loaded)
                }
            }

            PullRefreshIndicator(
                dogsState.isLoading,
                refreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun Empty() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.empty_breeds))
    }
}

@Composable
fun Error(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = error)
    }
}

@Composable
fun Success(successData: List<Breed>, favoriteBreed: (Breed) -> Unit, drillDown: (Breed, String) -> Unit) {
    DogList(breeds = successData, drillDown, favoriteBreed)
}

@Composable
fun DogList(breeds: List<Breed>, onItemClick: (Breed, String) -> Unit, onFavoriteClick: (Breed) -> Unit) {
    LazyColumn {
        items(breeds) { breed ->
            DogRow(
                breed = breed,
                onClick = onItemClick,
                onFavoriteClick = onFavoriteClick,
            )
            Divider()
        }
    }
}

@Composable
fun DogRow(breed: Breed, onClick: (Breed, String) -> Unit, onFavoriteClick: (Breed) -> Unit) {
    Row(
        Modifier
            .clickable {
                onClick(
                    breed,
                    LocalDateTime
                        .now()
                        .toString()
                )
            }
            .padding(10.dp)
    ) {
        Text(breed.name, Modifier.weight(1F))
        FavoriteIcon(breed) {
            onFavoriteClick(breed)
        }
    }
}

@Composable
fun FavoriteIcon(breed: Breed, onFavoriteClick: (Breed) -> Unit) {
    Crossfade(
        modifier = Modifier.clickable { onFavoriteClick(breed) },
        targetState = !breed.favorite,
        animationSpec = TweenSpec(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "CrossFadeFavoriteIcon"
    ) { fav ->
        if (fav) {
            Image(
                painter = painterResource(id = R.drawable.ic_favorite_border_24px),
                contentDescription = stringResource(R.string.favorite_breed, breed.name)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_favorite_24px),
                contentDescription = stringResource(R.string.unfavorite_breed, breed.name)
            )
        }
    }
}

@Composable
fun DetailsScreen(
    onBack: () -> Unit = {},
    breedId: String = "",
    optionalText: String = "",
    pictureViewModel: PictureViewModel = koinViewModel { parametersOf(breedId) },
) {
    val state by pictureViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(pictureViewModel) {
        pictureViewModel.activate()
    }
    BackHandler {
        onBack()
    }
    Column {
        when (state) {
            is PictureViewState.Content -> {
                Text(text = "Showing details for breed ID -> $breedId")
                Text(text = "Optional text is: $optionalText")
                val url = (state as PictureViewState.Content).pictureUrl
                Text(text = "Pic URL is: $url")
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = url,
                    contentDescription = "Translated description of what the image contains"
                )
            }
            PictureViewState.Error -> {
                Text(text = "Error while loading")
            }
            PictureViewState.Loading -> {
                Text(text = "Loading")
            }
        }
    }
}
