package com.owl_laugh_at_wasted_time.domain.repository


interface MultiChoiceState<T> {
    fun isChecked(item: T): Boolean
    val totalCheckedCount: Int
}