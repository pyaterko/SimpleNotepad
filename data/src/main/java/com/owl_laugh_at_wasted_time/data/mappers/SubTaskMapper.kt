package com.owl_laugh_at_wasted_time.data.mappers

import com.owl_laugh_at_wasted_time.data.entity.ShoppingListItemDbModel
import com.owl_laugh_at_wasted_time.data.entity.SubTaskItemDbModel
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import com.owl_laugh_at_wasted_time.domain.entity.SubTaskItem
import javax.inject.Inject

class SubTaskMapper @Inject constructor() {

    fun mapEntityToDbModel(item: SubTaskItem) = SubTaskItemDbModel(
        id = item.id,
        text = item.text,
        done = item.done,
        idParent = item.idParent
    )

    fun mapDbModelToEntity(itemDbModel: SubTaskItemDbModel) = SubTaskItem(
        id = itemDbModel.id,
        text = itemDbModel.text,
        done = itemDbModel.done,
        idParent = itemDbModel.idParent
    )

    fun mapListDbModelToListEntity(list: List<SubTaskItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}