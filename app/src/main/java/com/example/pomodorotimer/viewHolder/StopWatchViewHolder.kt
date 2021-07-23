package com.example.pomodorotimer.viewHolder

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodorotimer.MainActivity
import com.example.pomodorotimer.R
import com.example.pomodorotimer.model.StopWatchModel
import com.example.pomodorotimer.interfaces.StopWatcherListener
import com.example.pomodorotimer.databinding.ViewHolderRecycleBinding
import com.example.pomodorotimer.utills.Utills
import com.example.pomodorotimer.utills.Utills.displayTime

class StopWatchViewHolder(

    private val binding: ViewHolderRecycleBinding,
    private val listener: StopWatcherListener

): RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(stopwatch: StopWatchModel) {
        if(stopwatch.currentMs == -1L)  binding.container.setBackgroundColor(ContextCompat.getColor(binding.container.context, R.color.end_timer))
        else {
            if(stopwatch.forDifference != 0L && stopwatch.isStarted) {
                stopwatch.currentMs -= System.currentTimeMillis() - stopwatch.forDifference

            }
            binding.container.setBackgroundColor(ContextCompat.getColor(binding.container.context, R.color.white))
            binding.customProgress.setPeriod(stopwatch.startMs)
            binding.customProgress.setCurrent(stopwatch.startMs - stopwatch.currentMs)
            binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
        }
        if (stopwatch.isStarted) {
            startTimer(stopwatch)
        }
        else {
            stopTimer(stopwatch)
        }
        initButtonsListeners(stopwatch)
    }

    private fun startTimer(stopwatch: StopWatchModel) {
        binding.startPauseButton.text = Utills.STOP
        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()
        binding.blinkingIndicator.isInvisible = false
        (binding.blinkingIndicator.background as? AnimationDrawable)?.start()
    }

    private fun stopTimer(stopwatch: StopWatchModel) {
        binding.startPauseButton.text = Utills.START
        stopwatch.isStarted = false
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
            binding.container.setBackgroundColor(ContextCompat.getColor(binding.container.context, R.color.white))
            listener.reset(stopwatch.id)
        }

        binding.deleteButton.setOnClickListener { listener.delete(stopwatch.id) }
    }


    private fun getCountDownTimer(stopwatch: StopWatchModel): CountDownTimer {

        return object : CountDownTimer(Utills.PERIOD, Utills.UNIT_MS) {
            val interval = Utills.UNIT_MS
            override fun onTick(millisUntilFinished: Long) {
                stopwatch.forDifference = System.currentTimeMillis()
                stopwatch.currentMs -= interval
                binding.customProgress.setCurrent(stopwatch.startMs - stopwatch.currentMs)
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                //TODO("Костыль, поправлю")
                if(stopwatch.currentMs <= 0L) {
                    stopTimer(stopwatch)
                    binding.customProgress.setCurrent(0L)
                    stopwatch.currentMs = stopwatch.startMs
                    binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
                    stopwatch.forDifference = 0L
                    stopwatch.currentMs = -1L
                    binding.container.setBackgroundColor(ContextCompat.getColor(binding.container.context, R.color.end_timer))
                    listener.timerEnd(stopwatch.id)
                }
            }

            override fun onFinish() {
                binding.stopwatchTimer.text = stopwatch.currentMs.displayTime()
            }
        }
    }

}