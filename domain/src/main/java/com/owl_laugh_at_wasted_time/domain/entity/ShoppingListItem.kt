package com.owl_laugh_at_wasted_time.domain.entity

import com.owl_laugh_at_wasted_time.domain.UNDEFINED_ID

data class ShoppingListItem(
    val id:Int = UNDEFINED_ID,
    val text: String = "",
    var done: Boolean = false
)