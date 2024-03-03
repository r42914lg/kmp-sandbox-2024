package co.touchlab.kampkit.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.touchlab.kampkit.db.Breed
import co.touchlab.kermit.Logger

@Composable
fun MyAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = screenListRoute,
    log: Logger,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
    ) {
        screenListNavGraphBuilder(
            log,
            navController::navigateToDetails,
            navController::popBackStack,
        )
        screenDetailsNavGraphBuilder(
            navController::popBackStack,
        )
    }
}

/**
 * Screen List navigation
 */

const val screenListRoute = "screen_list"

fun NavGraphBuilder.screenListNavGraphBuilder(
    log: Logger,
    onToDetails: (Breed, String) -> Unit,
    onBack: () -> Unit
) {
    composable(screenListRoute) {
        MainScreen(
            log = log,
            onToDetails = onToDetails,
            onBack = onBack,
        )
    }
}

/**
 * Screen Details navigation
 */

const val screenDetailsRoute = "screen_Details"
const val someMandatoryArg = "arg_1"
const val someOptionalArg = "arg_2"

fun NavGraphBuilder.screenDetailsNavGraphBuilder(
    onBack: () -> Unit
) {
    composable(
        route = "$screenDetailsRoute/{$someMandatoryArg}?$someOptionalArg={$someOptionalArg}",
        arguments = listOf(
            navArgument(someMandatoryArg) { type = NavType.StringType },
            navArgument(someOptionalArg) {
                type = NavType.StringType
                nullable = false
                defaultValue = "some default value"
            }
        )
    ) {
        val mandatoryArg = it.arguments?.getString(someMandatoryArg) ?: throw IllegalStateException()
        val optionalArg = it.arguments?.getString(someOptionalArg).orEmpty()
        DetailsScreen(
            onBack = onBack,
            breedId = mandatoryArg,
            optionalText = optionalArg,
        )
    }
}

fun NavController.navigateToDetails(breed: Breed, optionalArgVal: String) {
    navigate("$screenDetailsRoute/${breed.name}?$someOptionalArg=${optionalArgVal}")
}
