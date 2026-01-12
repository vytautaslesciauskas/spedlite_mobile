package lt.agmis.spedlite.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import lt.agmis.spedlite.model.Task


sealed interface Screen : NavKey {

    @Serializable
    class Login() : Screen

    @Serializable
    class Tasks() : Screen

    @Serializable
    class Settings() : Screen

    @Serializable
    class TaskDetails(val task: Task) : Screen
}
