package com.owl_laugh_at_wasted_time.domain.entity

data class NotesListItem(
    override val originItem: ItemNote,
    override val isChecked: Boolean
) : ListItem<ItemNote> {
    val id: Int get() = originItem.id
    val title: String get() = originItem.title
    val text: String get() = originItem.text
    val color: ItemColor get() = originItem.color
    val dateOfCreation: String get() = originItem.dateOfCreation
    val category: String get() = originItem.category
}