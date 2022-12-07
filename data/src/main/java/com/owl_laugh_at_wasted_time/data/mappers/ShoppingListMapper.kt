package com.owl_laugh_at_wasted_time.data.mappers

import com.owl_laugh_at_wasted_time.data.entity.ShoppingListItemDbModel
import com.owl_laugh_at_wasted_time.domain.entity.ShoppingListItem
import javax.inject.Inject

class ShoppingListMapper @Inject constructor() {

    fun mapEntityToDbModel(item: ShoppingListItem) = ShoppingListItemDbModel(
        id = item.id,
        text = item.text,
        done = item.done
    )

   private fun mapDbModelToEntity(itemDbModel: ShoppingListItemDbModel) = ShoppingListItem(
        id = itemDbModel.id,
        text = itemDbModel.text,
        done = itemDbModel.done
    )

    fun mapListDbModelToListEntity(list: List<ShoppingListItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}