package lt.agmis.spedlite.fake

import lt.agmis.spedlite.network.ChangePasswordResponse
import lt.agmis.spedlite.network.LocationUpdateResponse
import lt.agmis.spedlite.network.LoginResponse
import lt.agmis.spedlite.network.SpedliteApi
import lt.agmis.spedlite.network.StatusChangeResponse
import lt.agmis.spedlite.network.TaskDto
import lt.agmis.spedlite.network.TasksResponse

/**
 * Fake implementation of SpedliteApi for testing purposes
 */
class FakeSpedliteApi : SpedliteApi {

    // Tracking variables
    var lastTaskId: Long? = null
    var lastStatus: Int? = null

    // Configurable responses
    var statusChangeResponse = StatusChangeResponse(true, TaskDto(1, "234", 2.3, 4.2, "ss", 1, 1, null))

    // Error simulation
    var shouldThrowException = false
    var exceptionToThrow: Exception = RuntimeException("Test exception")

    override suspend fun changeTaskStatus(taskId: Long, status: Int): StatusChangeResponse {
        if (shouldThrowException) {
            throw exceptionToThrow
        }
        lastTaskId = taskId
        lastStatus = status
        return statusChangeResponse
    }

    // Unused methods - throw NotImplementedError
    override suspend fun login(username: String, password: String): LoginResponse {
        throw NotImplementedError("Not needed for this test")
    }

    override suspend fun getTasks(): TasksResponse {
        throw NotImplementedError("Not needed for this test")
    }

    override suspend fun updateLocation(latitude: Double, longitude: Double): LocationUpdateResponse {
        throw NotImplementedError("Not needed for this test")
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        newPasswordConfirm: String
    ): ChangePasswordResponse {
        throw NotImplementedError("Not needed for this test")
    }

    override suspend fun logout() {
        throw NotImplementedError("Not needed for this test")
    }
}