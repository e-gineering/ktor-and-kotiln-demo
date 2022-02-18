package com.egineering.ktordemo.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse (
    val id: Int,
    val name: String,
    val projectId: Int
)
