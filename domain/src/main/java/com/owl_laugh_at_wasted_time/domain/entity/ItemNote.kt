package com.owl_laugh_at_wasted_time.domain.entity


import java.text.SimpleDateFormat
import java.util.*


data class ItemNote(

    var id: Int = UNDEFINED_ID,
    var title: String = "",
    var text: String = "",
    var color: ItemColor = ItemColor.WHITE,
    var dateOfCreation: String = SimpleDateFormat(
        DATE_TIME_FORMAT,
        Locale.getDefault()
    ).format(Date()),
    var category: Boolean = true,
    var priority: PriorityToDo = PriorityToDo.NOT,
    var data: String = ""
) {
    companion object {
        const val DATE_TIME_FORMAT = "dd.MMM.YYY HH:mm"
        const val UNDEFINED_ID = 0
    }


}