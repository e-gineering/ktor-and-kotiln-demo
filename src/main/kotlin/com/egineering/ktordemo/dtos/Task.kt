package com.egineering.ktordemo.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val name: String
) {
    constructor(taskResponse: TaskResponse) : this(taskResponse.id, taskResponse.name)
}
