package com.example.localproxy.service.proxy

import android.util.Log
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SocketListener(port: Int) {
    private var server: ServerSocket = ServerSocket(port)
    private var requestHandler: OnSocketRequest? = RequestHandler()

    suspend fun start() = withContext(Dispatchers.IO) {
        while (true) {
            try {
                Log.e(TAG, "ServerSocket ready to connect")
                val socket: Socket = server.accept()
                Log.e(TAG, "ServerSocket accepted")
                runBlocking { requestHandler?.handle(socket) }
                Log.e(TAG, "Request handled")
            } catch (e: SocketException) {
                if (e.message == "Socket closed") Log.e(TAG, e.message!!)
                else Log.e(TAG, "start() exception", e)
                break
            }
        }
    }

    fun stop() {
        requestHandler = null
        server.close()
    }

    interface OnSocketRequest {
        fun handle(socket: Socket)
    }

    companion object {
        const val TAG = "SocketListener"
    }
}