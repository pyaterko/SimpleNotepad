package com.owl_laugh_at_wasted_time.viewmodel.notes.multichoice

import javax.inject.Inject


class State<T> @Inject constructor(
    val totalCount: Int,
    val totalCheckedCount: Int,
    val list: List<T>,
    val selectAllOperation: SelectAllOperation
)