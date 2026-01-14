package lt.agmis.spedlite.usecase

import io.github.aakira.napier.Napier
import lt.agmis.spedlite.model.Task
import lt.agmis.spedlite.network.SpedliteApi
import lt.agmis.spedlite.util.runCatchingCoroutine

class FetchTasksUseCase(
    private val spedliteApi: SpedliteApi
) {

    suspend fun fetchTasks(): Result<List<Task>> {
        return runCatchingCoroutine {
            spedliteApi.getTasks().tasks.map { Task(it) }
        }
            .onFailure {
                Napier.e("Failed to fetch tasks", it)
            }
    }
}