package lt.agmis.spedlite.navigation

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf

@Stable
class AppNavigator(startDestination: Screen) {
    val backStack = mutableStateListOf<Screen>(startDestination)

    fun navigate(screen: Screen) {
        backStack.add(screen)
    }

    fun setRoot(screen: Screen) {
        backStack.clear()
        backStack.add(screen)
    }

    fun back() {
        backStack.removeLastOrNull()
    }

    fun singleTop(screen: Screen) {
        val last = backStack.lastOrNull()
        if (last?.javaClass?.simpleName == screen.javaClass?.simpleName) {
            backStack.remove(last)
        }
        backStack.add(screen)
    }
}