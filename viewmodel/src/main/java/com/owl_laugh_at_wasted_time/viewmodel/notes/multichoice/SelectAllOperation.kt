package com.owl_laugh_at_wasted_time.viewmodel.notes.multichoice

import javax.inject.Inject

class SelectAllOperation @Inject constructor(
    val titleRes: Int,
    val operation: () -> Unit
)