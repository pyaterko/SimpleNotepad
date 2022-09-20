package com.owl_laugh_at_wasted_time.domain.entity


import com.owl_laugh_at_wasted_time.domain.DATE_TIME_FORMAT
import com.owl_laugh_at_wasted_time.domain.UNDEFINED_ID
import java.text.SimpleDateFormat
import java.util.*


data class ItemToDo(

    var id: Int = UNDEFINED_ID,
    var title: String = "",
    var text: String = "",
    var color: ItemColor = ItemColor.WHITE,
    var dateOfCreation: String = SimpleDateFormat(
        DATE_TIME_FORMAT,
        Locale.getDefault()
    ).format(Date()),
    var priority: PriorityToDo = PriorityToDo.NOT,
    var data: String = ""
)