package com.example.pomodorotimer.interfaces

import com.example.pomodorotimer.model.StopWatchModel

interface StopWatcherListener {
    fun start(id: Int)

    fun stop(id: Int, currentMs: Long)

    fun reset(id: Int)

    fun delete(id: Int)

    fun timerEnd(id: Int)

}