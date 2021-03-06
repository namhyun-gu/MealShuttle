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
package com.earlier.yma.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.earlier.yma.R
import com.earlier.yma.data.MealResponse
import com.earlier.yma.ui.base.AppBar
import com.earlier.yma.ui.base.Center
import com.earlier.yma.ui.base.ContentPanel
import com.earlier.yma.ui.theme.MealViewerTheme
import com.earlier.yma.util.DateUtils
import com.earlier.yma.util.parseAllergyInfo
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.buttons
import com.vanpra.composematerialdialogs.datetime.datepicker.datepicker
import java.util.Date

@Composable
fun MainActivityContent(
    modifier: Modifier = Modifier,
    onNavigateSetting: () -> Unit
) {
    val viewModel: MainViewModel = viewModel()
    val uiState: MainUiState by viewModel.uiState.collectAsState(MainUiState.Loading)
    val uiEvent: MainUiEvent by viewModel.uiEvent.collectAsState(MainUiEvent.None)

    var currentType by rememberSaveable { mutableStateOf(MealType.Lunch) }
    var currentDate by rememberSaveable { mutableStateOf(Date()) }

    val dateDialog = remember { MaterialDialog() }

    dateDialog.build {
        datepicker(
            title = stringResource(R.string.dialog_select_date),
            initialDate = DateUtils.convertLocalDate(currentDate),
        ) { newDate ->
            currentDate = DateUtils.convertDate(newDate)
            viewModel.loadContent(currentType, currentDate)
        }
        buttons {
            positiveButton(res = R.string.action_ok)
            negativeButton(res = R.string.action_cancel)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            MainTopBar(
                type = currentType,
                date = currentDate,
                onDateSelect = {
                    dateDialog.show()
                },
                onSettingSelect = {
                    onNavigateSetting()
                }
            )
        },
        bottomBar = {
            MainBottomBar(
                selectType = currentType,
                onTypeSelect = { newType ->
                    currentType = newType
                    viewModel.loadContent(currentType, currentDate)
                }
            )
        }
    ) { innerPadding ->
        MainContent(
            modifier = Modifier.padding(innerPadding),
            uiState = uiState
        )
    }
}

@Composable
fun MainTopBar(
    type: MealType,
    date: Date,
    onDateSelect: () -> Unit = {},
    onSettingSelect: () -> Unit = {}
) {
    AppBar(
        title = {
            Text(
                stringResource(type.stringResId),
            )
        },
        subtitle = {
            Text(
                DateUtils.formatDate(date, "MM.dd"),
            )
        },
        expandSpace = {
            Icon(
                type.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp),
                tint = Color(0xFFFFD600),
            )
        },
        actions = {
            IconButton(onClick = onDateSelect) {
                Icon(
                    Icons.Rounded.Event,
                    null,
                    tint = MaterialTheme.colors.onSurface
                )
            }
            IconButton(onClick = onSettingSelect) {
                Icon(
                    Icons.Rounded.Settings,
                    null,
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainTopBar_Preview() {
    MealViewerTheme {
        Column {
            MainTopBar(
                type = MealType.Lunch,
                date = Date(),
            )
            Divider()
            Spacer(Modifier.height(8.dp))
            Divider()
            MainTopBar(
                type = MealType.Dinner,
                date = Date(),
            )
        }
    }
}

@Composable
fun MainBottomBar(
    selectType: MealType,
    onTypeSelect: (MealType) -> Unit = {}
) {
    val types = MealType.values()

    Row(
        modifier = Modifier
            .padding(
                horizontal = 16.dp,
                vertical = 16.dp
            )
            .fillMaxWidth(),
    ) {
        types.forEach { type ->
            val isSelected = (type == selectType)

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                MainBottomBarItem(
                    text = stringResource(type.stringResId),
                    icon = type.icon,
                    selected = isSelected
                ) {
                    onTypeSelect(type)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainBottomBarItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val selectedColor by animateColorAsState(
        if (selected) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.onSurface
        }
    )

    val backgroundColor by animateColorAsState(
        if (selected) {
            MaterialTheme.colors.primary.copy(
                alpha = 0.24f
            )
        } else {
            Color.Transparent
        }
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(32.dp),
        color = backgroundColor,
        modifier = modifier
            .height(48.dp)
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = selectedColor,
            )
            if (selected) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text,
                    style = MaterialTheme.typography.body2.copy(
                        color = selectedColor
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MealBottomBar_Preview() {
    Column {
        MainBottomBar(selectType = MealType.Breakfast)
        Spacer(Modifier.height(8.dp))
        MainBottomBar(selectType = MealType.Lunch)
        Spacer(Modifier.height(8.dp))
        MainBottomBar(selectType = MealType.Dinner)
    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
) {
    when (uiState) {
        MainUiState.Loading -> {
            Center(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
        is MainUiState.Success -> {
            MealContent(
                modifier = modifier,
                meal = uiState.content
            )
        }
        is MainUiState.Error -> {
            val errorMessage = if (uiState.exception is IllegalArgumentException) {
                stringResource(R.string.msg_meal_empty)
            } else {
                stringResource(R.string.msg_meal_error)
            }

            Center(modifier = Modifier.fillMaxSize()) {
                Text(errorMessage, style = MaterialTheme.typography.body1)
            }
        }
    }
}

@Composable
fun MealContent(
    modifier: Modifier,
    meal: MealResponse.Meal
) {
    Box(modifier = modifier.padding(horizontal = 16.dp)) {
        LazyColumn(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            item {
                ContentPanel {
                    CalorieItem(calorie = meal.calorie)
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ContentPanel {
                    DishList(
                        modifier = Modifier.fillMaxWidth(),
                        dishList = meal.dishList
                    )
                }
            }
        }
    }
}

@Composable
fun DishList(
    modifier: Modifier = Modifier,
    dishList: List<String>
) {
    Column(modifier = modifier) {
        dishList.forEach { food ->
            DishItem(
                food = food
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DishItem(
    modifier: Modifier = Modifier,
    food: String
) {
    val allergyNames = stringArrayResource(R.array.allergy_info)
    val (foodName, allergyInfo) = parseAllergyInfo(food)
    val allergyMessage = allergyInfo.joinToString { allergyNames[it] }

    var secondaryText: @Composable (() -> Unit)? = null
    if (allergyInfo.isNotEmpty()) {
        secondaryText = {
            Text(allergyMessage)
        }
    }

    ListItem(
        modifier = modifier,
        secondaryText = secondaryText
    ) {
        Text(foodName)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CalorieItem(
    modifier: Modifier = Modifier,
    calorie: String
) {
    ListItem(
        modifier = modifier,
        icon = {
            Icon(
                Icons.Rounded.LocalFireDepartment,
                contentDescription = null,
                tint = Color(0xFFD50000),
            )
        },
        secondaryText = {
            Text(calorie)
        }
    ) {
        Text(stringResource(R.string.subtitle_calorie))
    }
}

@Preview(showBackground = true)
@Composable
fun CalorieItem_Preview() {
    MealViewerTheme {
        CalorieItem(calorie = "0 Kcal")
    }
}
