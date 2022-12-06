package com.owl_laugh_at_wasted_time.data.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.owl_laugh_at_wasted_time.domain.entity.ItemCategory

@Entity(tableName = "category_table")
data class ItemCategoriesDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String
) {
    fun toItemCategory() = ItemCategory(
        id = id,
        name = name,
        state = false
    )

    companion object {
        fun toItemCategoriesDbModel(item: ItemCategory) = ItemCategoriesDbModel(
            id = item.id,
            name = item.name
        )
    }
}