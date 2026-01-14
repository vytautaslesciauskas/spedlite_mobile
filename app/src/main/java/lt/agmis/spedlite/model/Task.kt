package lt.agmis.spedlite.model

import kotlinx.serialization.Serializable
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

    fun getTaskMessageForChangingStatus(getString: (Int) -> String): String {
        val taskTypeName = type.toStringRes()?.let { getString(it) } ?: typeRaw
        return when (status) {
            TaskStatus.Pending -> "Do you want to start task?\n${taskTypeName}"
            TaskStatus.InProgress -> "Do you want to finish task\n${taskTypeName}"
            TaskStatus.Finished -> "Do you want to restart task?\n${taskTypeName}"
            TaskStatus.Aborted -> "Do you want to restart task?\n${taskTypeName}"
            TaskStatus.Unknown -> "Do you want to restart task?\n${taskTypeName}"
        }
    }
}
