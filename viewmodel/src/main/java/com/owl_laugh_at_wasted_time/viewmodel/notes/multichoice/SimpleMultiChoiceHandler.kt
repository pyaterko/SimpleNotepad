package com.owl_laugh_at_wasted_time.viewmodel.notes.multichoice

import com.owl_laugh_at_wasted_time.domain.repository.MultiChoiceHandler
import com.owl_laugh_at_wasted_time.domain.repository.MultiChoiceState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class SimpleMultiChoiceHandler<T> @Inject constructor() : MultiChoiceHandler<T>,
    MultiChoiceState<T> {

    private val checkedIds = HashSet<Int>()
    private var items: List<T> = emptyList()
    private val stateFlow = MutableStateFlow(Any())

    override fun setItemsFlow(coroutineScope: CoroutineScope, itemsFlow: Flow<List<T>>) {
        coroutineScope.launch {
            itemsFlow.collectLatest { list ->
                items = list
                removeDeletedCats(list)
                notifyUpdates()
            }
        }
    }

    override fun listen(): Flow<MultiChoiceState<T>> {
        return stateFlow.map { this }
    }

    override fun isChecked(item: T): Boolean {
        return checkedIds.contains(item.hashCode())
    }

    override fun toggle(item: T) {
        if (isChecked(item)) {
            clear(item)
        } else {
            check(item)
        }
    }

    override fun check(item: T) {
        if (!exists(item)) return
        checkedIds.add(item.hashCode())
        notifyUpdates()
    }

    override fun clear(item: T) {
        if (!exists(item)) return
        checkedIds.remove(item.hashCode())
        notifyUpdates()
    }

    override fun selectAll() {
        checkedIds.addAll(items.map { it.hashCode() })
        notifyUpdates()
    }

    override fun clearAll() {
        checkedIds.clear()
        notifyUpdates()
    }

    override val totalCheckedCount: Int
        get() = checkedIds.size

    private fun exists(item: T): Boolean {
        return items.any { it.hashCode() == item.hashCode() }
    }

    private fun removeDeletedCats(cats: List<T>) {
        val existingIds = HashSet(cats.map { it.hashCode() })
        val iterator = checkedIds.iterator()
        while (iterator.hasNext()) {
            val id = iterator.next()
            if (!existingIds.contains(id)) {
                iterator.remove()
            }
        }
    }

    private fun notifyUpdates() {
        stateFlow.value = Any()
    }

}