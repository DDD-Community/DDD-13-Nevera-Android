package com.anddd.nevera.infra.notification.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.anddd.nevera.domain.model.notification.AppNotificationType
import com.anddd.nevera.domain.repository.NotificationRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class NotificationSaveWorkerTest {

    private val context = mockk<Context>(relaxed = true)

    private fun createWorker(
        inputData: Data,
        repository: NotificationRepository = mockk(relaxed = true),
        runAttemptCount: Int = 0,
    ): Pair<NotificationSaveWorker, NotificationRepository> {
        val workerParams = mockk<WorkerParameters>(relaxed = true) {
            every { this@mockk.inputData } returns inputData
            every { this@mockk.runAttemptCount } returns runAttemptCount
        }
        val worker = NotificationSaveWorker(context, workerParams, repository)
        return worker to repository
    }

    @Test
    fun `정상 input이면 알림을 저장하고 Result_success를 반환한다`() = runTest {
        val repository = mockk<NotificationRepository>(relaxed = true)
        val inputData = NotificationSaveWorker.createInputData(
            id = "notification-id",
            type = AppNotificationType.DEFAULT,
            title = "title",
            subtitle = "message",
            createdAt = 1_700_000_000_000L,
            deeplink = "nevera://detail/1",
        )
        val (worker, _) = createWorker(inputData, repository)

        val result = worker.doWork()

        assertEquals(Result.success(), result)
        coVerify(exactly = 1) {
            repository.insert(
                match {
                    it.id == "notification-id" &&
                        it.type == AppNotificationType.DEFAULT &&
                        it.title == "title" &&
                        it.subtitle == "message" &&
                        it.createdAt == 1_700_000_000_000L &&
                        it.deeplink == "nevera://detail/1"
                }
            )
        }
    }

    @Test
    fun `type input이 알 수 없는 값이면 저장하지 않고 Result_failure를 반환한다`() = runTest {
        val repository = mockk<NotificationRepository>(relaxed = true)
        val inputData = workDataOf(
            "id" to "notification-id",
            "type" to "UNKNOWN_TYPE",
            "title" to "title",
            "created_at" to 1_700_000_000_000L,
            "deeplink" to "nevera://detail/1",
        )
        val (worker, _) = createWorker(inputData, repository)

        val result = worker.doWork()

        assertEquals(Result.failure(), result)
        coVerify(exactly = 0) { repository.insert(any()) }
    }

    @Test
    fun `필수 input이 누락되면 저장하지 않고 Result_failure를 반환한다`() = runTest {
        val repository = mockk<NotificationRepository>(relaxed = true)
        val inputData = workDataOf(
            "title" to "title",
            "type" to "DEFAULT",
            "created_at" to 1_700_000_000_000L,
            "deeplink" to "nevera://detail/1",
        )
        val (worker, _) = createWorker(inputData, repository)

        val result = worker.doWork()

        assertEquals(Result.failure(), result)
        coVerify(exactly = 0) { repository.insert(any()) }
    }

    @Test
    fun `repository insert 중 CancellationException이 발생하면 예외를 재전파한다`() = runTest {
        val repository = mockk<NotificationRepository> {
            coEvery { insert(any()) } throws CancellationException("Job was cancelled")
        }
        val inputData = NotificationSaveWorker.createInputData(
            id = "notification-id",
            type = AppNotificationType.DEFAULT,
            title = "title",
            subtitle = null,
            createdAt = 1_700_000_000_000L,
            deeplink = "nevera://detail/1",
        )
        val (worker, _) = createWorker(inputData, repository)

        assertThrows<CancellationException> {
            worker.doWork()
        }
    }

    @Test
    fun `repository insert가 실패하면 Result_retry를 반환한다`() = runTest {
        val repository = mockk<NotificationRepository> {
            coEvery { insert(any()) } throws IllegalStateException("insert failed")
        }
        val inputData = NotificationSaveWorker.createInputData(
            id = "notification-id",
            type = AppNotificationType.DEFAULT,
            title = "title",
            subtitle = null,
            createdAt = 1_700_000_000_000L,
            deeplink = "nevera://detail/1",
        )
        val (worker, _) = createWorker(inputData, repository)

        val result = worker.doWork()

        assertEquals(Result.retry(), result)
    }

    @Test
    fun `repository insert 실패 횟수가 MAX_RETRIES에 도달하면 Result_failure를 반환한다`() = runTest {
        val repository = mockk<NotificationRepository> {
            coEvery { insert(any()) } throws IllegalStateException("insert failed")
        }
        val inputData = NotificationSaveWorker.createInputData(
            id = "notification-id",
            type = AppNotificationType.DEFAULT,
            title = "title",
            subtitle = null,
            createdAt = 1_700_000_000_000L,
            deeplink = "nevera://detail/1",
        )
        val (worker, _) = createWorker(
            inputData = inputData,
            repository = repository,
            runAttemptCount = 5,
        )

        val result = worker.doWork()

        assertEquals(Result.failure(), result)
    }
}
