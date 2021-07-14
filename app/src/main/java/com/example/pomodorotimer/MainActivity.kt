package com.example.pomodorotimer

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodorotimer.adapters.StopWatchAdapter
import com.example.pomodorotimer.model.StopWatchModel
import com.example.pomodorotimer.databinding.ActivityMainBinding
import com.example.pomodorotimer.databinding.ViewHolderRecycleBinding
import com.example.pomodorotimer.interfaces.StopWatcherListener
import com.example.pomodorotimer.viewHolder.StopWatchViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), StopWatcherListener {

    //Variables
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingViewModel: ViewHolderRecycleBinding

    //Animation
    private val rotateOpen: Animation by lazy{ AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim)}
    private val rotateClose: Animation by lazy{AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim)}
    private val rotateOpenConfig: Animation by lazy{AnimationUtils.loadAnimation(this, R.anim.bottom_animation_speed)}
    private val rotateCloseConfig: Animation by lazy{AnimationUtils.loadAnimation(this, R.anim.top_animation)}
    private var clicked = false

    //StopWatch
    private val stopwatches = mutableListOf<StopWatchModel>()
    private val stopwatchAdapter = StopWatchAdapter()
    private var nextId = 0
    private var currentMinutes: Int = 0
    private var currentSeconds: Int = 0
    private var idTimerStart = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        bindingViewModel = ViewHolderRecycleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.red_main)

        binding.apply {
            recycler.layoutManager = LinearLayoutManager(recycler.context)
            recycler.adapter = stopwatchAdapter
            editMinutes.addTextChangedListener(getInputTextWatcher())
            editSecond.addTextChangedListener(getInputTextWatcher())
            addTimerBtn.setOnClickListener {
                addTimer(currentMinutes, currentSeconds)
            }
            floatingActionButton.setOnClickListener {
                onAddButtonClicked()
            }
        }
    }

    private fun checkEditToOpenButton(minutes: String, seconds: String) {
        binding.apply {
            addTimerBtn.isEnabled =
                (editMinutes.text?.isNotEmpty() == true || editSecond.text?.isNotEmpty() == true
                        && addTimerBtn.error == null && editSecond.error == null)
        }
        if (minutes.isNotEmpty()) {
            if (minutes.toInt() > MAX_TIME_MINUTES) {
                binding.editMinutes.error = NUMBER_EXCEEDS_RANGE
                binding.addTimerBtn.isEnabled = false
                return
            }
            binding.addTimerBtn.isEnabled = true
        }
        else {
            if (seconds.isNotEmpty()) {
                if (seconds.toInt() > MAX_TIME_SECONDS) {
                    binding.editSecond.error = NUMBER_EXCEEDS_RANGE
                    binding.addTimerBtn.isEnabled = false
                    return
                }
                binding.addTimerBtn.isEnabled = true
            }
        }
    }

    private fun getInputTextWatcher():TextWatcher {
        return object:TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val minutesString: String = binding.editMinutes.text.toString()
                val secondsString: String = binding.editSecond.text.toString()
                checkEditToOpenButton(minutesString, secondsString)
                currentMinutes = if(minutesString.isNotEmpty()) minutesString.toInt()
                else 0
                currentSeconds = if(secondsString.isNotEmpty()) secondsString.toInt()
                else 0

            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun onAddButtonClicked() {
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        binding.apply {
            if(clicked) {
                floatingActionButton.startAnimation(rotateClose)
                setTimer.startAnimation(rotateCloseConfig)
                setTimer.visibility = View.GONE
                this@MainActivity.hideKeyboard(currentFocus ?: View(this@MainActivity))
            }
            else {
                floatingActionButton.startAnimation(rotateOpen)
                setTimer.visibility = View.VISIBLE
                setTimer.startAnimation(rotateOpenConfig)
            }
        }
    }

    private fun addTimer(minutes: Int, seconds: Int) {
        val translateMs = (minutes * 60000 + seconds * 1000).toLong()
        stopwatches.add(StopWatchModel(nextId++, translateMs,false, translateMs, 0L))
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    override fun start(id: Int) {
        if(idTimerStart != -1) {
            val oldTimer = stopwatches.find { it.id == idTimerStart }
            changeStopwatch(idTimerStart, oldTimer?.currentMs ?: 0, false)
        }
        val currentTimer = stopwatches.find { it.id == id }
        if(currentTimer?.isStarted == true) currentTimer.currentMs = System.currentTimeMillis() - currentTimer.forDifference
        changeStopwatch(id, null, true)
        idTimerStart = id

//        var currentTimer = stopwatches.find { it.id == id }
//        currentTimer?.startTime = System.currentTimeMillis()
//        lifecycleScope.launch(Dispatchers.Main) {
//            while (true) {
//
//                bindingViewModel.stopwatchTimer.text = (currentTimer!!.currentMs - (System.currentTimeMillis() - currentTimer.startTime)).displayTime()
//                delay(INTERVAL)
//            }
//        }
    }

    override fun stop(id: Int, currentMs: Long) {
        changeStopwatch(id, currentMs, false)
        idTimerStart = -1
    }

    override fun reset(id: Int) {
        val currentTimer = stopwatches.find { it.id == id }
        changeStopwatch(id, currentTimer?.startMs ?: 0, false)

    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(id: Int, currentMs: Long?, isStarted: Boolean) {
        val newTimers = mutableListOf<StopWatchModel>()
        stopwatches.forEach {
            if (it.id == id) {
                newTimers.add(StopWatchModel(it.id, currentMs ?: it.currentMs, isStarted, it.startMs, it.forDifference))
            } else {
                newTimers.add(it)
            }
        }
        stopwatchAdapter.submitList(newTimers)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private companion object {
        private const val MAX_TIME_MINUTES = 5999
        private const val MAX_TIME_SECONDS = 59
        private const val NUMBER_EXCEEDS_RANGE = "Number is too large"
    }
}