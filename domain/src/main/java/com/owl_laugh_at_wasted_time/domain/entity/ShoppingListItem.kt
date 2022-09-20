package com.owl_laugh_at_wasted_time.domain.entity

data class ShoppingListItem(
    val id:Int = UNDEFINED_ID,
    val text: String = "",
    var done: Boolean = false
){
    companion object{
        const val UNDEFINED_ID = 0
    }
}