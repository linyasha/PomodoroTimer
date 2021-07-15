package com.example.pomodorotimer.viewHolder

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodorotimer.MainActivity
import com.example.pomodorotimer.R
import com.example.pomodorotimer.model.StopWatchModel
import com.example.pomodorotimer.interfaces.StopWatcherListener
import com.example.pomodorotimer.databinding.ViewHolderRecycleBinding

class StopWatchViewHolder(

    private val binding: ViewHolderRecycleBinding,
    private val listener: StopWatcherListener

): RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(stopwatch: StopWatchModel) {
        if(stopwatch.forDifference != 0L && stopwatch.isStarted) {
                stopwatch.currentMs -= System.currentTimeMillis() - stopwatch.forDifference

        }
        binding.customProgress.setPeriod(stopwatch.startMs)
        binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        }
        else {
            stopTimer(stopwatch)
        }
        initButtonsListeners(stopwatch)
    }

    private fun startTimer(stopwatch: StopWatchModel) {
        binding.startPauseButton.text = STOP
        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()
        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: StopWatchModel) {
        binding.startPauseButton.text = START
        timer?.cancel()
        stopwatch.forDifference = 0L
        binding.blinkingIndicator.isInvisible = true
        (binding.blinkingIndicator.background as? AnimationDrawable)?.stop()
    }

    private fun initButtonsListeners(stopwatch: StopWatchModel) {
        binding.startPauseButton.setOnClickListener {
            if (stopwatch.isStarted) {
                listener.stop(stopwatch.id, stopwatch.currentMs)

            } else {
                listener.start(stopwatch.id)

            }
        }

        binding.restartButton.setOnClickListener {
            if(stopwatch.currentMs <= 0) {
                binding.startPauseButton.isEnabled = true
                //binding.container.setBackgroundColor(getColor(binding.container.context, R.color.white))
            }

            listener.reset(stopwatch.id)
        }

        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }


    private fun getCountDownTimer(stopwatch: StopWatchModel): CountDownTimer {

        return object : CountDownTimer(PERIOD, UNIT_TEN_MS) {
            val interval = UNIT_TEN_MS
            @SuppressLint("ResourceAsColor")
            override fun onTick(millisUntilFinished: Long) {
                stopwatch.currentMs -= interval
                binding.customProgress.setCurrent(stopwatch.startMs - stopwatch.currentMs)
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                stopwatch.forDifference = System.currentTimeMillis()

                if(stopwatch.currentMs <= 0L) {
                    stopTimer(stopwatch)
                    binding.customProgress.setCurrent(0L)
//                    binding.container.setBackgroundColor(getColor(binding.container.context, R.color.end_timer))
                    binding.startPauseButton.isEnabled = false
                    listener.timerEnd(stopwatch.id)
                }
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()

            }
        }
    }


    private fun Long.displayTime(): String {
        if (this <= 0L) {
            return START_TIME
        }
        val h = this / 1000 / 3600
        val m = this / 1000 % 3600 / 60
        val s = this / 1000 % 60

        return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
    }

    private fun displaySlot(count: Long): String {
        return if (count / 10L > 0) {
            "$count"
        } else {
            "0$count"
        }
    }


    private companion object {
        private const val START_TIME = "00:00:00"
        private const val STOP = "STOP"
        private const val START = "START"
        private const val UNIT_TEN_MS = 1000L
        private const val PERIOD  = 1000L * 60L * 60L * 24L // Day
    }
}