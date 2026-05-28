package com.anddd.nevera.feature.ingredient.ocrcapture.component.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private val imageCapture: ImageCapture = ImageCapture.Builder().build()
    private var cameraProvider: ProcessCameraProvider? = null
    private var currentSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var currentLifecycleOwner: LifecycleOwner? = null
    private var currentSurfaceProvider: Preview.SurfaceProvider? = null

    suspend fun bindCamera(lifecycleOwner: LifecycleOwner, surfaceProvider: Preview.SurfaceProvider) {
        currentLifecycleOwner = lifecycleOwner
        currentSurfaceProvider = surfaceProvider
        bindWith(lifecycleOwner, surfaceProvider, currentSelector)
    }

    suspend fun swapCamera() {
        currentSelector = if (currentSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        val owner = currentLifecycleOwner ?: return
        val provider = currentSurfaceProvider ?: return
        bindWith(owner, provider, currentSelector)
    }

    private suspend fun bindWith(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider,
        selector: CameraSelector,
    ) = suspendCancellableCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(context)
        continuation.invokeOnCancellation { future.cancel(true) }
        future.addListener({
            runCatching {
                val provider = future.get()
                cameraProvider = provider
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = surfaceProvider
                }
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
            }.onSuccess {
                continuation.resume(Unit)
            }.onFailure {
                continuation.resumeWithException(it)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    suspend fun takePicture(): Uri = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            imageCapture.takePicture(
                Dispatchers.IO.asExecutor(),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        try {
                            val uri = image.toBitmapCorrected().saveToTempFile()
                            if (continuation.isActive) continuation.resume(uri)
                        } catch (e: Exception) {
                            if (continuation.isActive) continuation.resumeWithException(e)
                        } finally {
                            image.close()
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        if (continuation.isActive) continuation.resumeWithException(exception)
                    }
                },
            )
        }
    }

    private fun ImageProxy.toBitmapCorrected(): Bitmap {
        val bitmap = toBitmap()
        val rotation = imageInfo.rotationDegrees
        if (rotation == 0) return bitmap
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // cacheDir 사용: 업로드 완료 후 시스템이 자동으로 정리하는 임시 저장소
    private fun Bitmap.saveToTempFile(): Uri {
        val dir = File(context.cacheDir, "ocr_captures").also { it.mkdirs() }
        val file = File(dir, "capture_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { compress(Bitmap.CompressFormat.JPEG, 95, it) }
        return Uri.fromFile(file)
    }

    fun release() {
        cameraProvider?.unbindAll()
        currentLifecycleOwner = null
        currentSurfaceProvider = null
    }
}