package lt.agmis.spedlite.usecase

import io.github.aakira.napier.Napier
import lt.agmis.spedlite.model.Task
import lt.agmis.spedlite.network.SpedliteApiClient
import lt.agmis.spedlite.util.runCatchingCoroutine

class FetchTasksUseCase(
    private val spedliteApiClient: SpedliteApiClient
) {

    suspend fun fetchTasks(): Result<List<Task>> {
        return runCatchingCoroutine {
            spedliteApiClient.getTasks().tasks.map { Task(it) }
        }
            .onFailure {
                Napier.e("Failed to fetch tasks", it)
            }
    }
}