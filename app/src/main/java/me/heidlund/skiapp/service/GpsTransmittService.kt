package me.heidlund.skiapp.service

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import me.heidlund.skiapp.models.SkiPoint
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.Types
import okhttp3.MediaType.Companion.toMediaType

class GpsTransmittService: Service() {

    private val MAX_POINTS = 20
    private val SUBMIT_ENDPOINT =  "http://192.168.0.24:8080/submit"
    private val pointStorage = ArrayList<SkiPoint>()
    private var addedItems = 0
    private val client = OkHttpClient()

    companion object {
        val MSG_ID: Int = 1
    }

    // Define the callback interface
    fun interface OnMessageReceivedListener {
        fun onMessageReceived(content: SkiPoint)
    }

    private var messageReceivedListener: OnMessageReceivedListener? = null

    // Allow other classes to set the listener
    fun setOnMessageReceivedListener(listener: OnMessageReceivedListener?) {
        this.messageReceivedListener = listener
    }

    // Handler to process incoming messages
    internal inner class IncomingHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_ID -> {
                    val data = msg.data
                    val content = data.getSerializable("data")
                    // Notify listener if set
                    if (messageReceivedListener != null && content is SkiPoint) {
                        messageReceivedListener!!.onMessageReceived(content as SkiPoint)
                    }
                }

                else -> super.handleMessage(msg)
            }
        }
    }

    private val messenger = Messenger(IncomingHandler())

    override fun onBind(intent: Intent): IBinder? {
        return messenger.binder
    }

    override fun onCreate() {
        super.onCreate()
        setOnMessageReceivedListener { content ->
            Log.d(
                "GpsTransmittService",
                "onMessageReceived: $content"
            )
            pointStorage.add(content)
            addedItems+=1
            if (addedItems > MAX_POINTS) {
                sendData()
                addedItems = 0
            }
        }
    }

    private fun pointsAsJson(): String {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val listType = Types.newParameterizedType(List::class.java, SkiPoint::class.java)
        val adapter = moshi.adapter<List<SkiPoint>>(listType)
        return adapter.toJson(pointStorage)
    }

    private fun sendData() {
        val lastPoint = pointStorage.size

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = pointsAsJson().toRequestBody(mediaType)
        Log.d("sendData: body",pointsAsJson())
        val request = Request.Builder()
            .post(body)
            .url(SUBMIT_ENDPOINT)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ON FAILURE REQUEST", "onFailure: ", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        println("Unexpected code $it")
                        Log.e("ON RESPONSE", it.body.toString(), )
                    } else {
                        for (i in 0 until lastPoint) {
                            pointStorage.removeAt(0)
                        }
                    }
                }
            }
        })
    }
}