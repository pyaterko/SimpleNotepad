package com.owl_laugh_at_wasted_time.data.mappers

import com.owl_laugh_at_wasted_time.data.entity.ItemNoteDbModel
import com.owl_laugh_at_wasted_time.domain.entity.ItemNote
import javax.inject.Inject


class ItemNoteListMapper @Inject constructor() {

    fun mapEntityToDbModel(item: ItemNote) = ItemNoteDbModel(
        id = item.id,
        title = item.title,
        text = item.text,
        color = item.color,
        dateOfCreation = item.dateOfCreation,
        category = item.category
    )

    fun mapDbModelToEntity(itemDbModel: ItemNoteDbModel) = ItemNote(
        id = itemDbModel.id,
        title = itemDbModel.title,
        text = itemDbModel.text,
        color = itemDbModel.color,
        dateOfCreation = itemDbModel.dateOfCreation,
        category = itemDbModel.category
    )

    fun mapListDbModelToListEntity(list: List<ItemNoteDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}
