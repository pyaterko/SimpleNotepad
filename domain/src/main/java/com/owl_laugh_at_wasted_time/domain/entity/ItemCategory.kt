package com.owl_laugh_at_wasted_time.domain.entity

import com.owl_laugh_at_wasted_time.domain.UNDEFINED_ID

data class ItemCategory(
    val id:Int= UNDEFINED_ID,
    val name:String="",
    var state:Boolean=false
)
