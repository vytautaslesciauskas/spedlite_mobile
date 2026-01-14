package lt.agmis.spedlite.usecase

import io.github.aakira.napier.Napier
import lt.agmis.spedlite.model.TaskStatus
import lt.agmis.spedlite.network.SpedliteApi
import lt.agmis.spedlite.network.StatusChangeResponse
import lt.agmis.spedlite.util.runCatchingCoroutine

class ChangeTaskStatusUseCase(
    private val spedliteApi: SpedliteApi
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
            spedliteApi.changeTaskStatus(taskId, newStatus)
        }
            .onFailure {
                Napier.e("Failed to change task status", it)
            }
    }
}