package lt.agmis.spedlite

import kotlinx.coroutines.test.runTest
import lt.agmis.spedlite.fake.FakeSpedliteApi
import lt.agmis.spedlite.model.TaskStatus
import lt.agmis.spedlite.usecase.ChangeTaskStatusUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChangeTaskStatusUseCaseTest {

    private lateinit var useCase: ChangeTaskStatusUseCase
    private lateinit var fakeApi: FakeSpedliteApi

    @Before
    fun setup() {
        fakeApi = FakeSpedliteApi()
        useCase = ChangeTaskStatusUseCase(fakeApi)
    }

    @Test
    fun `changeStatus from Pending sends IN_PROGRESS to api`() = runTest {
        // When
        val result = useCase.changeStatus(1L, TaskStatus.Pending)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1L, fakeApi.lastTaskId)
        assertEquals(TaskStatus.STATUS_IN_PROGRESS, fakeApi.lastStatus)
    }

    @Test
    fun `changeStatus from InProgress sends FINISHED to api`() = runTest {
        // When
        val result = useCase.changeStatus(1L, TaskStatus.InProgress)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1L, fakeApi.lastTaskId)
        assertEquals(TaskStatus.STATUS_FINISHED, fakeApi.lastStatus)
    }

    @Test
    fun `changeStatus from Finished sends PENDING to api`() = runTest {
        // When
        val result = useCase.changeStatus(1L, TaskStatus.Finished)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(TaskStatus.STATUS_PENDING, fakeApi.lastStatus)
    }

    @Test
    fun `changeStatus from Aborted sends PENDING to api`() = runTest {
        // When
        val result = useCase.changeStatus(1L, TaskStatus.Aborted)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(TaskStatus.STATUS_PENDING, fakeApi.lastStatus)
    }

    @Test
    fun `changeStatus from Unknown sends PENDING to api`() = runTest {
        // When
        val result = useCase.changeStatus(1L, TaskStatus.Unknown)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(TaskStatus.STATUS_PENDING, fakeApi.lastStatus)
    }

    @Test
    fun `changeStatus returns failure when api throws exception`() = runTest {
        // Given
        fakeApi.shouldThrowException = true
        fakeApi.exceptionToThrow = RuntimeException("Network error")

        // When
        val result = useCase.changeStatus(1L, TaskStatus.Pending)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `changeStatus passes correct taskId to api`() = runTest {
        // When
        useCase.changeStatus(12345L, TaskStatus.Pending)

        // Then
        assertEquals(12345L, fakeApi.lastTaskId)
    }
}

