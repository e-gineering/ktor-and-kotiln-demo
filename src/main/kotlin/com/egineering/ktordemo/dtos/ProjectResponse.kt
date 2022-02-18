package com.egineering.ktordemo.dtos

import kotlinx.serialization.Serializable

@Serializable
data class ProjectResponse(
    val id: Int,
    val name: String
)
