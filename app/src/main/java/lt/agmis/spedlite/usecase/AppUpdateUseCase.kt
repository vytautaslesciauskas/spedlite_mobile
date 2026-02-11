package lt.agmis.spedlite.usecase

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.readByteArray
import lt.agmis.spedlite.BuildConfig
import lt.agmis.spedlite.network.SpedliteApi
import lt.agmis.spedlite.util.runCatchingCoroutine
import java.io.File

class AppUpdateUseCase(
    private val spedliteApi: SpedliteApi,
    private val application: Application
) {
    sealed interface UpdateResult {
        object NoUpdate : UpdateResult
        data class NewUpdate(val versionCode: Int, val downloadUrl: String) : UpdateResult
        object Failed : UpdateResult
    }

    suspend fun checkForAppUpdate(): UpdateResult {
        return runCatchingCoroutine {
            val response = spedliteApi.getAppVersion()
            if (response.version > BuildConfig.VERSION_CODE) {
                UpdateResult.NewUpdate(response.version, response.url)
            } else {
                UpdateResult.NoUpdate
            }
        }
            .onFailure {
                Napier.e("Failed to check for app update", it)
            }
            .getOrDefault(UpdateResult.Failed)
    }

    suspend fun downloadApkFile(
        fileUrl: String,
        onProgress: (Int) -> Unit
    ): Result<File> {
        return runCatchingCoroutine {
            onProgress(0)
            val apkFile = File(application.filesDir, "update/application.apk")
            apkFile.mkdirs()
            if (apkFile.exists()) {
                apkFile.delete()
            }
            spedliteApi.downloadApk(fileUrl).execute { httpResponse ->
                val channel: ByteReadChannel = httpResponse.body()
                val contentLength = httpResponse.headers[HttpHeaders.ContentLength]?.toLongOrNull() ?: 0L
                apkFile.outputStream().use { output ->
                    var totalBytesRead = 0L
                    var lastProgressUpdate = 0L

                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (!packet.exhausted()) {
                            val bytes = packet.readByteArray()
                            output.write(bytes)
                            totalBytesRead += bytes.size

                            if (contentLength > 0 && (totalBytesRead - lastProgressUpdate > 100_000)) {

                                val progress = ((totalBytesRead * 100) / contentLength).toInt()

                                withContext(Dispatchers.Main) {
                                    onProgress(progress)
                                }

                                lastProgressUpdate = totalBytesRead
                            }
                        }
                    }
                }
            }
            apkFile
        }
            .onFailure { exception ->
                Napier.e("Failed to download new version. apkFileUrl ${fileUrl}", exception)
            }
    }

    fun installApk(apkFile: File): Result<Unit> {
        return runCatchingCoroutine {
            val uri = FileProvider.getUriForFile(application, "${BuildConfig.APPLICATION_ID}.provider", apkFile)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            application.startActivity(intent)
        }
            .onFailure { exception ->
                Napier.e(
                    "Failed to install new version, apkFile ${apkFile.absolutePath} appVersion ${BuildConfig.VERSION_CODE}", exception
                )
            }
    }
}