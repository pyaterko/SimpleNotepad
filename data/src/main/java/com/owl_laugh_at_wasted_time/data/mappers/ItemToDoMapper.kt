package com.owl_laugh_at_wasted_time.data.mappers

import com.owl_laugh_at_wasted_time.data.entity.ItemToDoDbModel
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import javax.inject.Inject


class ItemToDoMapper @Inject constructor() {

    fun mapEntityToDbModel(item: ItemToDo) = ItemToDoDbModel(
        id = item.id,
        title = item.title,
        text = item.text,
        color = item.color,
        dateOfCreation = item.dateOfCreation,
        priority = item.priority,
        data = item.data
    )

    fun mapDbModelToEntity(itemDbModel: ItemToDoDbModel) = ItemToDo(
        id = itemDbModel.id,
        title = itemDbModel.title,
        text = itemDbModel.text,
        color = itemDbModel.color,
        dateOfCreation = itemDbModel.dateOfCreation,
        priority = itemDbModel.priority,
        data = itemDbModel.data
    )

    fun mapListDbModelToListEntity(list: List<ItemToDoDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}
