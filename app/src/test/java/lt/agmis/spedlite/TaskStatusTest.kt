package lt.agmis.spedlite

import lt.agmis.spedlite.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskStatusTest {

    @Test
    fun `parse STATUS_PENDING returns Pending`() {
        val result = TaskStatus.parse(TaskStatus.STATUS_PENDING)
        assertEquals(TaskStatus.Pending, result)
    }

    @Test
    fun `parse STATUS_IN_PROGRESS returns InProgress`() {
        val result = TaskStatus.parse(TaskStatus.STATUS_IN_PROGRESS)
        assertEquals(TaskStatus.InProgress, result)
    }

    @Test
    fun `parse STATUS_FINISHED returns Finished`() {
        val result = TaskStatus.parse(TaskStatus.STATUS_FINISHED)
        assertEquals(TaskStatus.Finished, result)
    }

    @Test
    fun `parse STATUS_ABORTED returns Aborted`() {
        val result = TaskStatus.parse(TaskStatus.STATUS_ABORTED)
        assertEquals(TaskStatus.Aborted, result)
    }

    @Test
    fun `parse unknown status returns Unknown`() {
        val result = TaskStatus.parse(99)
        assertEquals(TaskStatus.Unknown, result)
    }
}