package com.app.thinktwice.database.utils

import kotlinx.datetime.Clock

object TimeProvider {
    fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
}