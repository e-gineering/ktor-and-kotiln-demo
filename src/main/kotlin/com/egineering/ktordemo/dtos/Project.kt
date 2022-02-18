package com.egineering.ktordemo.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: Int,
    val name: String,
    val tasks: List<Task>,
    val categories: List<Category>
)
