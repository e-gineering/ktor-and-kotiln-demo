package com.egineering.ktordemo.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val name: String
) {
    constructor(categoryResponse: CategoryResponse) : this(categoryResponse.id, categoryResponse.name)
}
