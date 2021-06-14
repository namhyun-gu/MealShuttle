/*
 * Copyright 2021 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.earlier.yma.ui.search

import com.earlier.yma.data.model.School

sealed class SearchUiState {
    object Idle : SearchUiState()

    object Loading : SearchUiState()

    data class Success(
        val keyword: String,
        val schoolList: List<School>,
        val orgList: List<String>,
        val filterOrg: Set<String> = emptySet(),
        val page: Int = 1
    ) : SearchUiState()

    data class Error(
        val exception: Exception,
    ) : SearchUiState()

    fun isIdle() = this is Idle

    fun isLoading() = this is Loading

    fun isSuccess() = this is Success

    fun isError() = this is Error

    fun isEmpty() = this is Success && this.schoolList.isEmpty()
}