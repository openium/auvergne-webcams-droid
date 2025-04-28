package fr.openium.auvergnewebcams.ext

import androidx.annotation.IdRes
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

fun NavBackStackEntry?.isLifecycleResumed(): Boolean =
    this?.lifecycle?.currentState == Lifecycle.State.RESUMED

fun NavController.popBackStackWithLifecycle() {
    if (currentBackStackEntry.isLifecycleResumed()) {
        popBackStack()
    }
}

fun NavController.popBackStackWithLifecycle(
    @IdRes destinationId: Int,
    inclusive: Boolean,
) {
    if (currentBackStackEntry.isLifecycleResumed()) {
        popBackStack(
            destinationId = destinationId,
            inclusive = inclusive,
            saveState = false
        )
    }
}

inline fun <reified T : Any> NavController.popBackStackWithLifecycle(
    inclusive: Boolean,
    saveState: Boolean = false,
) {
    if (currentBackStackEntry.isLifecycleResumed()) {
        popBackStack<T>(
            inclusive = inclusive,
            saveState = saveState
        )
    }
}

fun NavController.navigateWithLifecycle(
    route: Any,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
) {
    if (currentBackStackEntry.isLifecycleResumed()) {
        navigate(
            route,
            navOptions,
            navigatorExtras
        )
    }
}

inline fun <reified R : Any> NavController.navigateToDestinationAndPopUpTo(
    routeToNavigateTo: Any
) {
    navigate(routeToNavigateTo) {
        popUpTo<R> {
            inclusive = true
        }
    }
}