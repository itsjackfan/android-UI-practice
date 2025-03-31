/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.reply.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reply.data.Email
import com.example.reply.data.EmailsRepository
import com.example.reply.data.EmailsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReplyHomeViewModel(
    private val emailsRepository: EmailsRepository = EmailsRepositoryImpl()
): ViewModel() {

    // UI state exposed to the UI
    private val _uiState = MutableStateFlow(ReplyHomeUIState(loading = true))
    val uiState: StateFlow<ReplyHomeUIState> = _uiState

    init {
        observeEmails()
    }

    private fun observeEmails() {
        viewModelScope.launch {
            emailsRepository.getAllEmails()
                .catch { ex ->
                    _uiState.value = ReplyHomeUIState(error = ex.message)
                }
                .collect { emails ->
                    _uiState.value = ReplyHomeUIState(emails = emails)
                }
        }
    }

    fun starEmail(email: Email) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                val updatedEmails = currentState.emails.map { existingEmail ->
                    if (existingEmail.id == email.id) {
                        existingEmail.copy(isStarred = !existingEmail.isStarred)
                    } else {
                        existingEmail
                    }
                }
                val updatedSelectedEmail = if (currentState.selected?.id == email.id) {
                    email.copy(isStarred = !email.isStarred)
                } else {
                    currentState.selected
                }
                currentState.copy(emails = updatedEmails, selected = updatedSelectedEmail)
            }
        }
    }

    fun setSelectedEmail(email: Email) {
        _uiState.update {
            currentState -> currentState.copy(selected = email)
        }
    }

    fun clearEmail() {
        _uiState.update {
            currentState -> currentState.copy(selected = null, replyTo = null, replyAll = false)
        }
    }

    fun setReplyingEmail(email: Email, isReplyAll: Boolean = false) {
        _uiState.update {
            currentState -> currentState.copy(replyTo = email, replyAll = isReplyAll)
        }
    }

    fun clearReplyingEmail() {
        _uiState.update {
            currentState -> currentState.copy(replyTo = null, replyAll = false)
        }
    }
}

data class ReplyHomeUIState(
    val emails : List<Email> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val selected: Email? = null,
    val replyTo: Email? = null,
    val replyAll: Boolean = false
)
