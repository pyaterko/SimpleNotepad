package com.owl_laugh_at_wasted_time.domain.entity

interface ListItem<T> {
    val originItem: T
    val isChecked: Boolean
}