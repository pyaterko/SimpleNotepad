package com.owl_laugh_at_wasted_time.data.mappers

import com.owl_laugh_at_wasted_time.data.entity.ItemToDoDbModel
import com.owl_laugh_at_wasted_time.domain.entity.ItemToDo
import javax.inject.Inject


class ItemToDoMapper @Inject constructor() {

    fun mapEntityToDbModel(item: ItemToDo) = item.id?.let {
        ItemToDoDbModel(
        id = it,
        title = item.title,
        done = item. done  ,
        color = item.color,
        dateOfCreation = item.dateOfCreation,
        data = item.data
    )
    }

    fun mapDbModelToEntity(itemDbModel: ItemToDoDbModel) = ItemToDo(
        id = itemDbModel.id,
        title = itemDbModel.title,
        done = itemDbModel. done  ,
        color = itemDbModel.color,
        dateOfCreation = itemDbModel.dateOfCreation,
        data = itemDbModel.data
    )

    fun mapListDbModelToListEntity(list: List<ItemToDoDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}
