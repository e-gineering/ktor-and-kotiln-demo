package com.egineering.ktordemo.dtos

import kotlinx.serialization.Serializable

@Serializable
data class TaskResponse(
    val id: Int,
    val name: String,
    val projectId: Int
)
