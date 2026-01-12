package lt.agmis.spedlite.model

import kotlinx.serialization.Serializable
import lt.agmis.spedlite.network.TaskDto


@Serializable
data class Task(
    val id: String,
    val cid: String,
    val deviceId: String,
    val licence: String,
    val address: String,
    val lat: String,
    val lng: String,
    val type: String,         // LOADING / UNLOADING etc.
    val queue: String,
    val visited: String,      // timestamp as string (can be negative)
    val client: String,
    val con_id: String,
    val status: String        // "0" / "1" etc.
) {
    constructor(dto: TaskDto) : this(
        id = dto.id,
        cid = dto.cid,
        deviceId = dto.deviceId,
        licence = dto.licence,
        address = dto.address,
        lat = dto.lat,
        lng = dto.lng,
        type = dto.type,
        queue = dto.queue,
        visited = dto.visited,
        client = dto.client,
        con_id = dto.con_id,
        status = dto.status
    )
}
