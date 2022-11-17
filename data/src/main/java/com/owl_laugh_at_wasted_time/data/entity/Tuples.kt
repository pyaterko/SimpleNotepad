package com.owl_laugh_at_wasted_time.data.entity

import java.util.*

data class ItemDoneUpdate(
    val id: UUID,
    var done: Boolean
)

data class SubItemDoneUpdate(
    val id: String,
    var done: Boolean
)