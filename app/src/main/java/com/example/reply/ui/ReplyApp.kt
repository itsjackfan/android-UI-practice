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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.reply.data.Email

@Composable
fun ReplyApp(
    replyHomeUIState: ReplyHomeUIState,
    onEmailClick: (Email) -> Unit,
    onStarClick: (Email) -> Unit,
    onReply: (Email, Boolean) -> Unit,
    onDismissReply: () -> Unit,
    onSendReply: (String) -> Unit,
    onClearEmail: () -> Unit
) {
    ReplyNavigationWrapperUI(onClearEmail = onClearEmail) {
        ReplyAppContent(
            replyHomeUIState = replyHomeUIState,
            onEmailClick = onEmailClick,
            onStarClick = onStarClick,
            onReply = onReply,
            onDismissReply = onDismissReply,
            onSendReply = onSendReply
        )
    }
}

@Composable
private fun ReplyNavigationWrapperUI(
    onClearEmail: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    var selectedDestination: ReplyDestination by remember {
        mutableStateOf(ReplyDestination.Inbox)
    }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isCompact = windowSizeClass.windowWidthSizeClass.toString() == "WindowWidthSizeClass: COMPACT"

    if (isCompact) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                content()
            }

            NavigationBar(modifier = Modifier.fillMaxWidth()) {
                ReplyDestination.entries.forEach {
                    NavigationBarItem(
                        selected = it == selectedDestination,
                        onClick = { 
                            selectedDestination = it 
                            if (it == ReplyDestination.Inbox) {
                                onClearEmail()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = stringResource(it.labelRes)
                            )
                        },
                        label = {
                            Text(text = stringResource(it.labelRes))
                        },
                    )
                }
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            NavigationRail(
                modifier = Modifier.fillMaxHeight(),
                containerColor = MaterialTheme.colorScheme.inverseOnSurface
            ) {
                ReplyDestination.entries.forEach {
                    NavigationRailItem(
                        selected = it == selectedDestination,
                        onClick = { 
                            selectedDestination = it 
                            if (it == ReplyDestination.Inbox) {
                                onClearEmail()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = stringResource(it.labelRes)
                            )
                        },
                        label = {
                            Text(text = stringResource(it.labelRes))
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                content()
            }
        }
    }
}

@Composable
fun ReplyAppContent(
    replyHomeUIState: ReplyHomeUIState,
    onEmailClick: (Email) -> Unit,
    onStarClick: (Email) -> Unit,
    onReply: (Email, Boolean) -> Unit,
    onSendReply: (String) -> Unit,
    onDismissReply: () -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isCompact = windowSizeClass.windowWidthSizeClass.toString() == "WindowWidthSizeClass: COMPACT"

    if (isCompact) {
        if (replyHomeUIState.replyTo != null) {
            ReplyPane(
                email = replyHomeUIState.replyTo,
                onSendReply = onSendReply,
                onDismissReply = onDismissReply,
                isReplyAll = replyHomeUIState.replyAll
            )
        } else if (replyHomeUIState.selected != null) {
            ReplyDetailPane(
                replyHomeUIState = replyHomeUIState,
                email = replyHomeUIState.selected,
                modifier = Modifier
                    .fillMaxHeight(),
                onStarClick = onStarClick,
                onReply = onReply,
            )
        } else {
            ReplyListPane(
                replyHomeUIState = replyHomeUIState,
                onEmailClick = onEmailClick,
                onStarClick = onStarClick
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            if (replyHomeUIState.replyTo != null) {
                ReplyDetailPane(
                    replyHomeUIState = replyHomeUIState,
                    email = replyHomeUIState.replyTo,
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight(),
                    onStarClick = onStarClick,
                    onReply = onReply
                )

                ReplyPane(
                    email = replyHomeUIState.replyTo,
                    onSendReply = onSendReply,
                    onDismissReply = onDismissReply,
                    isReplyAll = replyHomeUIState.replyAll,
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                )
            } else if (replyHomeUIState.selected != null) {
                ReplyListPane(
                    replyHomeUIState = replyHomeUIState,
                    onEmailClick = onEmailClick,
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight(),
                    onStarClick = onStarClick
                )

                ReplyDetailPane(
                    replyHomeUIState = replyHomeUIState,
                    email = replyHomeUIState.selected,
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight(),
                    onStarClick = onStarClick,
                    onReply = onReply,
                )
            } else {
                ReplyListPane(
                    replyHomeUIState = replyHomeUIState,
                    onEmailClick = onEmailClick,
                    modifier = Modifier
                        .fillMaxHeight(),
                    onStarClick = onStarClick
                )
            }
        }
    }
}
