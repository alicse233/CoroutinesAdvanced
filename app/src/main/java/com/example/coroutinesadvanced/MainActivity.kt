package com.example.coroutinesadvanced

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

/*
* @Author - Ali
* Date - 19 Oct 2021
* */

private val TAG = "MainAct"
private val BASE_URL = "https://test.io"

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

//        Now comes practice with retrofit
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApi::class.java)

//        Getting list using old way
//        Here enque func will start call in separate thread
        api.getListOldWay().enqueue(object : Callback<List<Person>> {
            override fun onResponse(call: Call<List<Person>>, response: Response<List<Person>>) {
                if (response.isSuccessful) {
//                    do this
                    Log.d(TAG, "Success received in old way call")
                }
            }

            override fun onFailure(call: Call<List<Person>>, t: Throwable) {
                Log.d(TAG, "Error received in old way call")
            }
        })

//        Now doing it in new coroutine way
//        which is given by:
        GlobalScope.launch(Dispatchers.IO) {
            val response = api.getListCoroutineWay().await()
            Log.d(TAG, response.toString())

//            If we want this to be in response body format
//            instead of await() just put awaitResponse()
//            like given below

            val responseBody = api.getListCoroutineWay().awaitResponse()
            if (responseBody.isSuccessful) {

            }

//            There is better approach if we make our api interface func as suspend
//            then we don't need to make it awaitResponse() just await and get response
//            because that is a suspend func returning response

            val advResponse = api.getListCoroutineUpdatedWay()
            if (advResponse.isSuccessful) {
                
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