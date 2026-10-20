package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AuditHistoryEntity
import com.example.data.model.AccountAuditResult
import com.example.data.network.TikTokRepository
import com.example.domain.analyzer.TikTokSecurityAnalyzer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AuditUiState {
    object Idle : AuditUiState
    data class Loading(val username: String) : AuditUiState
    data class Success(val result: AccountAuditResult) : AuditUiState
    data class Error(val message: String, val lastQuery: String) : AuditUiState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TikTokRepository()
    private val database = AppDatabase.getInstance(application)
    private val dao = database.auditHistoryDao()

    private val _uiState = MutableStateFlow<AuditUiState>(AuditUiState.Idle)
    val uiState: StateFlow<AuditUiState> = _uiState.asStateFlow()

    private val _inputQuery = MutableStateFlow("")
    val inputQuery: StateFlow<String> = _inputQuery.asStateFlow()

    val historyList: StateFlow<List<AuditHistoryEntity>> = dao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var activeJob: Job? = null
    private var lastClickTime = 0L

    fun onQueryChanged(newQuery: String) {
        _inputQuery.value = newQuery
    }

    fun analyzeAccount(rawQuery: String? = null) {
        val queryToAnalyze = rawQuery ?: _inputQuery.value
        val cleanUser = repository.extractUsername(queryToAnalyze)

        if (cleanUser.isBlank()) {
            _uiState.value = AuditUiState.Error(
                message = "يرجى كتابة اسم مستخدم أو رابط صالح لحساب تيك توك",
                lastQuery = queryToAnalyze
            )
            return
        }

        // Anti-spam fast clicking prevention
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < 1000 && _uiState.value is AuditUiState.Loading) {
            return
        }
        lastClickTime = currentTime

        activeJob?.cancel()
        activeJob = viewModelScope.launch {
            _uiState.value = AuditUiState.Loading(cleanUser)
            _inputQuery.value = cleanUser

            val result = repository.fetchAccount(cleanUser)
            result.fold(
                onSuccess = { account ->
                    val auditResult = TikTokSecurityAnalyzer.analyze(account)
                    _uiState.value = AuditUiState.Success(auditResult)

                    // Persist to local database history
                    try {
                        dao.insertAudit(
                            AuditHistoryEntity(
                                username = account.username,
                                nickname = account.nickname,
                                avatarUrl = account.avatarUrl,
                                followerCount = account.followerCount,
                                followingCount = account.followingCount,
                                likesCount = account.likesCount,
                                videoCount = account.videoCount,
                                isVerified = account.isVerified,
                                isPrivate = account.isPrivate,
                                safetyScore = auditResult.safetyScore,
                                ratingLevel = auditResult.ratingLevel
                            )
                        )
                    } catch (e: Exception) {
                        // Non-fatal database save error
                    }
                },
                onFailure = { error ->
                    val errorMessage = when {
                        error is NoSuchElementException -> error.message ?: "لم يتم العثور على الحساب. يرجى التأكد من اسم المستخدم."
                        error.message?.contains("Unable to resolve host") == true -> "تعذر الاتصال بالإنترنت. يرجى التحقق من الشبكة وإعادة المحاولة."
                        else -> error.message ?: "حدث خطأ غير متوقع أثناء فحص الحساب. يرجى المحاولة لاحقاً."
                    }
                    _uiState.value = AuditUiState.Error(
                        message = errorMessage,
                        lastQuery = cleanUser
                    )
                }
            )
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            dao.deleteById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }

    fun resetToIdle() {
        _uiState.value = AuditUiState.Idle
    }
}
