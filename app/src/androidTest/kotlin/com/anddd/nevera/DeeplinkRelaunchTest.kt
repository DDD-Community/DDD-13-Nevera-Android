package com.anddd.nevera

import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.anddd.nevera.navigation.DeeplinkTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 딥링크로 실행된 Activity가 설정 변경으로 다시 만들어질 때의 동작을 고정한다.
 *
 * Activity가 다시 만들어져도 getIntent()는 최초의 딥링크 intent를 그대로 돌려주고,
 * MainViewModel은 설정 변경을 넘어 살아남는다. 그래서 onCreate에서 조건 없이 intent를
 * 처리하면 같은 딥링크가 두 번 발행되어, 사용자가 그 사이 옮겨 둔 화면을 덮어쓴다.
 */
@RunWith(AndroidJUnit4::class)
class DeeplinkRelaunchTest {

    @Test
    fun `설정 변경으로 다시 만들어져도 딥링크는 한 번만 발행된다`() {
        val context = ApplicationProvider.getApplicationContext<NeveraApplication>()
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(DEEPLINK)
        }

        val received = mutableListOf<DeeplinkTarget>()
        val job = SupervisorJob()

        try {
            ActivityScenario.launch<MainActivity>(intent).use { scenario ->
                lateinit var viewModel: MainViewModel
                scenario.onActivity { activity ->
                    viewModel = ViewModelProvider(activity)[MainViewModel::class.java]
                }

                // 채널은 BUFFERED라, onCreate에서 발행된 목적지는 구독 전에도 보관된다.
                collectInto(viewModel, received, job)
                waitUntilIdle()
                assertEquals("최초 실행에서 한 번 발행되어야 한다", 1, received.size)

                // 최초 실행 때와 같은 intent로 Activity가 다시 만들어진다.
                scenario.recreate()
                scenario.onActivity { activity ->
                    // 재생성 후에도 최초의 딥링크 intent가 그대로 남아 있다.
                    assertEquals(Intent.ACTION_VIEW, activity.intent.action)
                    assertEquals(DEEPLINK, activity.intent.data?.toString())
                    // ViewModel은 설정 변경을 넘어 같은 인스턴스로 살아남는다.
                    assertEquals(viewModel, ViewModelProvider(activity)[MainViewModel::class.java])
                }
                waitUntilIdle()

                assertEquals("재생성으로 다시 발행되면 안 된다", 1, received.size)
            }
        } finally {
            job.cancel()
        }
    }

    @Test
    fun `재생성 뒤에 도착한 새 딥링크는 그대로 발행된다`() {
        val context = ApplicationProvider.getApplicationContext<NeveraApplication>()
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(DEEPLINK)
        }

        val received = mutableListOf<DeeplinkTarget>()
        val job = SupervisorJob()

        try {
            ActivityScenario.launch<MainActivity>(intent).use { scenario ->
                lateinit var viewModel: MainViewModel
                scenario.onActivity { activity ->
                    viewModel = ViewModelProvider(activity)[MainViewModel::class.java]
                }
                collectInto(viewModel, received, job)
                waitUntilIdle()

                scenario.recreate()
                waitUntilIdle()
                assertEquals(1, received.size)

                // 실행 중 알림을 누르면 새 intent가 onNewIntent로 도착한다.
                // NeveraMessagingService의 PendingIntent는 CLEAR_TOP과 SINGLE_TOP을 달아 두므로,
                // 이미 떠 있는 MainActivity가 새 instance 대신 onNewIntent를 받는다.
                val newIntent = Intent(context, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(OTHER_DEEPLINK)
                }
                scenario.onActivity { activity ->
                    instrumentation().callActivityOnNewIntent(activity, newIntent)
                }
                waitUntilIdle()

                assertEquals("회전 뒤 도착한 새 딥링크는 열려야 한다", 2, received.size)
                assertNotEquals(received[0], received[1])
            }
        } finally {
            job.cancel()
        }
    }

    private fun collectInto(
        viewModel: MainViewModel,
        into: MutableList<DeeplinkTarget>,
        job: Job,
    ) {
        CoroutineScope(Dispatchers.Main + job).launch {
            viewModel.deeplinkTargets.collect { into += it }
        }
    }

    private fun instrumentation(): Instrumentation =
        InstrumentationRegistry.getInstrumentation()

    private fun waitUntilIdle() {
        instrumentation().waitForIdleSync()
        Thread.sleep(SETTLE_MILLIS)
    }

    private companion object {
        const val DEEPLINK = "nevera://detail/101"
        const val OTHER_DEEPLINK = "nevera://detail/202"
        const val SETTLE_MILLIS = 500L
    }
}
