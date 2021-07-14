package com.example.pomodorotimer.model

data class StopWatchModel (
    val id: Int,
    var currentMs: Long,
    var isStarted: Boolean,
    var startMs: Long,
    var forDifference: Long
    )