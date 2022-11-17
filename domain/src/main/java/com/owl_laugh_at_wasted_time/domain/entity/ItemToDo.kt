package com.owl_laugh_at_wasted_time.domain.entity


import com.owl_laugh_at_wasted_time.domain.DATE_TIME_FORMAT
import com.owl_laugh_at_wasted_time.domain.UNDEFINED_ID
import java.text.SimpleDateFormat
import java.util.*

data class ItemToDo(
    var id: UUID? = UUID.randomUUID(),
    var title: String = "",
    var color: ItemColor = ItemColor.WHITE,
    var dateOfCreation: String = SimpleDateFormat(
        DATE_TIME_FORMAT,
        Locale.getDefault()
    ).format(Date()),
    var data: String = "",
    var done: Boolean=false
)