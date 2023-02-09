package com.example.localproxy.service.proxy

import android.util.Log
import java.io.OutputStream
import java.net.Socket
import java.net.SocketException
import okhttp3.Response

fun OutputStream.sendBodyStream(response: Response, socket: Socket) {
    response.body?.let { body ->
        val inputStream = body.byteStream()
        val buffer = ByteArray(100 * 1024)
        var bytesRead = inputStream.read(buffer)
        while (bytesRead != -1 && !socket.isClosed) {
            try {
                write(buffer, 0, bytesRead)
            } catch (e: SocketException) {
                Log.e("SendBodyStream", "sendBodyStream: error occurred: ${e.message}")
                break
            }
            bytesRead = inputStream.read(buffer)
        }
    }
}
