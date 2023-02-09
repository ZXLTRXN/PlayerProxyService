package com.example.localproxy.service.proxy

import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.Socket
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

class RequestHandler() : SocketListener.OnSocketRequest {
    private val client = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        )
        .build()

    private var url = TARGET_URL

    override fun handle(socket: Socket) {
        val offset = getOffsetFromPlayer(socket.getInputStream())
        Log.e(TAG, "required offset: $offset")
        val response = try {
            client.newCall(buildRequest(offset)).execute()
        } catch (e: Exception) {
            Log.e(TAG, "newCall().execute(): ", e)
            null
        }
        response?.let { resp ->
            socket.soTimeout = 10000
            sendResponseToPlayer(socket, resp)
        }
    }

    private fun sendResponseToPlayer(socket: Socket, response: Response) {
        val header = createCustomHeader(response)
        try {
            with(socket.getOutputStream()) {
                write(header)
                sendBodyStream(response, socket)
            }
        } catch (e: Exception) {
            Log.e(TAG, "SendResponseToPlayer: ", e)
        } finally {
            if (!socket.isClosed) socket.close()
            response.close()
        }
    }

    private fun buildRequest(offset: String): Request {
        val rangeHeader = "bytes=$offset-"
        return Request.Builder()
            .get()
            .url(url)
            .addHeader("Accept", "*/*")
            .addHeader("User-Agent", "stagefright/1.2 (Linux;Android 11)")
            .addHeader("Range", rangeHeader)
            .addHeader("Accept-Encoding", "gzip")
//            .addHeader("Connection", "Keep-Alive") // android 11 player requests this header too
            .build()
    }

    private fun createCustomHeader(response: Response): ByteArray {
        var header: String = when (val code = response.code) {
            200 -> CODE_200
            206 -> CODE_206
            else -> throw IllegalArgumentException("Response with code $code")
        }

        response.headers.forEach { (h, v) ->
            header += "$h: $v\r\n"
        }
        Log.w(TAG, "------TO player:\n$header ")
        header += "\r\n"

        return header.toByteArray()
    }


    private fun getOffsetFromPlayer(stream: InputStream): String {
        var offset = "0"
        val streamReader = InputStreamReader(stream)
        val bufferedReader = BufferedReader(streamReader)
        Log.w(TAG, "------FROM player")

        var i = 7
        while (i > 0) {
            val line = bufferedReader.readLine()
            if (line.startsWith("Range")) {
                val startIndex = line.indexOf('=') + 1
                val endIndex = line.indexOf('-')
                offset = line.substring(startIndex, endIndex)
            }
            Log.w(SocketListener.TAG, line)
            i--
        }
        return offset
    }

    private companion object {
        const val TAG = "RequestHandler"
        const val CODE_200 = "HTTP/1.1 200 OK\r\n"
        const val CODE_206 = "HTTP/1.1 206 Partial Content\r\n"
        const val TARGET_URL =
            "http://hls-ark-test.novotelecom.ru/test_films/wednesday_1080p_losfilm_s01_e01_zamena.mkv"
        const val TARGET_URL1 =
            "http://127.0.0.1:35662/play?xt=urn:tree:tiger:KY7Z6NYSD6R224NP2AHFGZYIP7JXGWWEGXAROIQ&dn=" +
                    "wednesday_1080p_losfilm_s01_e01_zamena.mkv&xl=3059150794&nodisk=1&auth_token=" +
                    "1736986710&watch_id=73887366-16aa-4a95-9c7d-eb9c0edab4c3"
    }
}
