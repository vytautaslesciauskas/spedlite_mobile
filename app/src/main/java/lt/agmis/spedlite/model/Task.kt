package lt.agmis.spedlite.model

import kotlinx.serialization.Serializable
import lt.agmis.spedlite.R
import lt.agmis.spedlite.network.TaskDto
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm")

@Serializable
data class Task(
    val id: Long,
    val address: String,
    val lat: Double,
    val lng: Double,
    val typeRaw: String,
    val visited: Long,
    val statusRaw: Int,
    val country: String? = null
) {
    constructor(dto: TaskDto) : this(
        id = dto.id,
        address = dto.address,
        lat = dto.lat,
        lng = dto.lng,
        typeRaw = dto.type,
        visited = dto.visited,
        statusRaw = dto.status,
        country = dto.country
    )

    val status: TaskStatus
        get() = TaskStatus.parse(statusRaw)

    val type: TaskType
        get() = TaskType.parse(typeRaw)

    val dateTime: String by lazy {
        try {
            val instant = Instant.ofEpochMilli(visited)
            instant.atZone(ZoneId.systemDefault()).format(dateTimeFormat)
        } catch (exception: Exception) {
            visited.toString()
        }
    }

    fun getTaskMessageForChangingStatus(getString: (Int, Array<Any>) -> String): String {
        val taskTypeName = type.toStringRes()?.let { getString(it, emptyArray()) } ?: typeRaw
        val messageRes = when (status) {
            TaskStatus.Pending -> R.string.task_confirm_start
            TaskStatus.InProgress -> R.string.task_confirm_finish
            TaskStatus.Finished -> R.string.task_confirm_restart
            TaskStatus.Aborted -> R.string.task_confirm_restart
            TaskStatus.Unknown -> R.string.task_confirm_restart
        }
        return getString(messageRes, arrayOf(taskTypeName))
    }

    fun getStatusText(): String {
        return when (status) {
            TaskStatus.Pending -> "Pending ${dateTime}"
            TaskStatus.InProgress -> "InProgress ${dateTime}"
            TaskStatus.Finished -> "Finished ${dateTime}"
            TaskStatus.Aborted -> "Aborted ${dateTime}"
            TaskStatus.Unknown -> "Unknown ${dateTime}"
        }
    }
}
