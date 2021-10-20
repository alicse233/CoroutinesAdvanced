package com.example.coroutinesadvanced

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

/*
* @Author - Ali
* Date - 19 Oct 2021
* */

private val TAG = "MainAct"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        If we have several suspend func and we execute them in a coroutine scope then
//        they will be execute in a sequential manner by default
//        which means first func will be executed first and second will be
//        executed once first is finished

        GlobalScope.launch(Dispatchers.IO) {

//            measure time mills is helper method that returns time took
//            these calls to get completed
//            Now In this block both are going parallel but its not right approach
//            Go to below code chunk to see this solved with async and await

            val time = measureTimeMillis {
                var ans1: String? = null
                var ans2: String? = null

                launch { ans1 = networkCall1()
                    Log.d(TAG, ans1!!) }
                launch { ans2 = networkCall2()
                    Log.d(TAG, ans2!!) }

            }

            Log.d(TAG, "Time took $time")


//            Now here lets make an asyncronous request here
            GlobalScope.launch(Dispatchers.IO) {
                val time = measureTimeMillis {

                    val ans1 = async { networkCall1() }
                    val ans2 = async { networkCall2() }

                    Log.d(TAG, ans1.await())
                    Log.d(TAG, ans2.await())
                }
                Log.d(TAG, "Time took using async $time")
            }

        }

//        Till now we were using GlobalScope
//        Which is not good practice as when we destroy our any component
//        like when activity is finished if you used global scope then
//        running coroutines will not be finished and Garbage collected
//        for that you can use life cycle scopes


        findViewById<TextView>(R.id.tvClick).setOnClickListener {
            lifecycleScope.launch {
                while (true) {
                    delay(1000L)
                    Log.d(TAG, "still running")
                }
            }

            GlobalScope.launch {
                delay(3000L)
                Intent(this@MainActivity, MainActivity2::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }
    }

    private suspend fun networkCall1(): String {
        delay(3000L)
        return "Answer 1"
    }

    private suspend fun networkCall2(): String {
        delay(3000L)
        return "Answer 2"
    }
}