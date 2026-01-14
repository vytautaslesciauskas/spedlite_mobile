package lt.agmis.spedlite.usecase

import io.github.aakira.napier.Napier
import lt.agmis.spedlite.model.TaskStatus
import lt.agmis.spedlite.network.SpedliteApiClient
import lt.agmis.spedlite.network.StatusChangeResponse
import lt.agmis.spedlite.util.runCatchingCoroutine

class ChangeTaskStatusUseCase(
    private val spedliteApiClient: SpedliteApiClient
) {
    suspend fun changeStatus(taskId: Long, status: TaskStatus): Result<StatusChangeResponse> {
        return runCatchingCoroutine {
            val newStatus = when (status) {
                TaskStatus.Pending -> TaskStatus.STATUS_IN_PROGRESS
                TaskStatus.InProgress -> TaskStatus.STATUS_FINISHED
                TaskStatus.Finished -> TaskStatus.STATUS_PENDING
                TaskStatus.Aborted -> TaskStatus.STATUS_PENDING
                TaskStatus.Unknown -> TaskStatus.STATUS_PENDING
            }
            spedliteApiClient.changeTaskStatus(taskId, newStatus)
        }
            .onFailure {
                Napier.e("Failed to change task status", it)
            }
    }
}