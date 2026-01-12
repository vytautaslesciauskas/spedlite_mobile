package lt.agmis.spedlite

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import lt.agmis.spedlite.network.ErrorBody

sealed interface Event {
    class Unauthorized(val errorBody: ErrorBody?) : Event
}

class EventDispatcher {

    private val internalEvents = MutableSharedFlow<Event>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val events: Flow<Event>
        get() = internalEvents

    fun tryEmit(event: Event) {
        internalEvents.tryEmit(event)
    }

    suspend fun emit(event: Event) {
        internalEvents.emit(event)
    }
}