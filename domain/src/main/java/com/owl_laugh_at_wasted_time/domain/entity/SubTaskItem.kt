package com.owl_laugh_at_wasted_time.domain.entity

import java.util.*


data class SubTaskItem(
    val id: Int,
    val idParent: UUID,
    val text: String,
    var done: Boolean
)