package fr.openium.auvergnewebcams.ui.webcamDetail

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import fr.openium.auvergnewebcams.R
import fr.openium.auvergnewebcams.ext.getUrlForWebcam
import fr.openium.auvergnewebcams.model.entity.Webcam
import fr.openium.auvergnewebcams.repository.WebcamRepository
import fr.openium.auvergnewebcams.service.DownloadWorker
import fr.openium.auvergnewebcams.ui.navigation.Destination
import fr.openium.auvergnewebcams.utils.DateUtils
import fr.openium.auvergnewebcams.utils.PreferencesUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


class ViewModelWebcamDetail(savedStateHandle: SavedStateHandle) : ViewModel(), KoinComponent {

    private val _state by lazy { MutableStateFlow<State>(State.Loading) }
    val state: StateFlow<State> by lazy {
        _state.asStateFlow()
    }

    private val _errorMessage by lazy {
        MutableSharedFlow<Error>()
    }
    val errorMessage: SharedFlow<Error> by lazy {
        _errorMessage.asSharedFlow()
    }

    private val webcamId = savedStateHandle.toRoute(Destination.WebcamDetails::class).webcamId

    private val webcamRepository by inject<WebcamRepository>()
    val prefUtils: PreferencesUtils by inject()
    val dateUtils: DateUtils by inject()

    fun loadWebcam() {
        viewModelScope.launch {
            _state.value = State.Loading
            webcamRepository.getWebcamFlow(webcamId)
                .map { optionalWebcam -> optionalWebcam.value }
                .catch { e ->
                    Timber.e(e)
                }
                .collect { webcam ->
                    if (webcam != null) {
                        _state.value = State.Loaded(webcam)
                    }
                }
        }
    }


    fun shareWebcam(
        context: Context,
    ) {
        viewModelScope.launch {
            (state.value as? State.Loaded)?.let { state ->
                val webcam = state.webcam

                val url = webcam.getUrlForWebcam(canBeHD = true)

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "${webcam.title}\n$url")
                    putExtra(Intent.EXTRA_SUBJECT, webcam.title)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                val chooser = Intent.createChooser(intent, context.getString(R.string.generic_chooser))
                if (chooser.resolveActivity(context.packageManager) != null) {
                    context.startActivity(chooser)
                } else {
                    _errorMessage.emit(Error(R.string.generic_no_application_for_action))
                }
            }
        }
    }


    fun saveWebcam(
        context: Context,
    ) {
        viewModelScope.launch {
            (state.value as? State.Loaded)?.let { state ->
                val webcam = state.webcam
                val urlSrc =
                    webcam.getUrlForWebcam(canBeHD = true)

                val fileExtension = if (webcam.isVideo) "mp4" else "jpg"
                val sanitizedTitle = webcam.title.orEmpty().replace("\\s+".toRegex(), "_")
                val fileName = "${sanitizedTitle}_${System.currentTimeMillis()}.$fileExtension"

                try {
                    WorkManager.getInstance(context).enqueue(
                        OneTimeWorkRequestBuilder<DownloadWorker>().apply {
                            setInputData(Data.Builder().apply {
                                putString(DownloadWorker.KEY_PATH_URL, urlSrc)
                                putBoolean(DownloadWorker.KEY_IS_PHOTO, !webcam.isVideo)
                                putString(DownloadWorker.KEY_FILENAME, fileName)
                            }.build())
                        }.build()
                    )
                } catch (ex: Exception) {
                    Timber.e(ex)
                    _errorMessage.emit(Error(R.string.generic_error))
                }
            }
        }
    }

    sealed interface State {
        object Loading : State
        data class Loaded(val webcam: Webcam) : State
    }

    data class Error(@StringRes val messageId: Int)
}