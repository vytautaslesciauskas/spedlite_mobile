package lt.agmis.spedlite

import lt.agmis.spedlite.model.TaskType
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskTypeTest {

    @Test
    fun `parse LOADING returns Pickup`() {
        val result = TaskType.parse(TaskType.LOADING)
        assertEquals(TaskType.Pickup, result)
    }

    @Test
    fun `parse PICKUP returns Pickup`() {
        val result = TaskType.parse(TaskType.PICKUP)
        assertEquals(TaskType.Pickup, result)
    }

    @Test
    fun `parse DELIVERY returns Delivery`() {
        val result = TaskType.parse(TaskType.DELIVERY)
        assertEquals(TaskType.Delivery, result)
    }

    @Test
    fun `parse UNLOADING returns Delivery`() {
        val result = TaskType.parse(TaskType.UNLOADING)
        assertEquals(TaskType.Delivery, result)
    }

    @Test
    fun `parse CUSTOMS returns Customs`() {
        val result = TaskType.parse(TaskType.CUSTOMS)
        assertEquals(TaskType.Customs, result)
    }

    @Test
    fun `parse TECH_INSPECTION returns TechInspection`() {
        val result = TaskType.parse(TaskType.TECH_INSPECTION)
        assertEquals(TaskType.TechInspection, result)
    }

    @Test
    fun `parse REFUEL returns Refuel`() {
        val result = TaskType.parse(TaskType.REFUEL)
        assertEquals(TaskType.Refuel, result)
    }

    @Test
    fun `parse REST returns Rest`() {
        val result = TaskType.parse(TaskType.REST)
        assertEquals(TaskType.Rest, result)
    }

    @Test
    fun `parse unknown type returns Unknown`() {
        val result = TaskType.parse("INVALID_TYPE")
        assertEquals(TaskType.Unknown, result)
    }
}