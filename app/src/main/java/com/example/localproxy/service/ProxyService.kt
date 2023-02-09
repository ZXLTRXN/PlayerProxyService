package com.example.localproxy.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.localproxy.service.proxy.SocketListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ProxyService : Service() {

    private val helper: NotificationHelper by lazy { NotificationHelper(this) }
    private val job = SupervisorJob()
    private var state: State = State.STOPPED

    private val socketListener: SocketListener = SocketListener(PORT)

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.extras?.run {
            when (getParcelable<Command>(SERVICE_COMMAND) as Command) {
                is Command.Start -> startService()
                is Command.Stop -> stopService()
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        socketListener.stop()
        job.cancel()
        log("service destroyed")
    }

    private fun startService() {
        if (state == State.STOPPED) {
            log("startService")
            startForeground(NotificationHelper.NOTIFICATION_ID, helper.getNotification())
            CoroutineScope(job).launch { socketListener.start() }
            state = State.STARTED
        } else {
            log("startService: already started")
        }
    }

    private fun stopService() {
        if (state == State.STARTED) {
            log("stopService")
            stopForeground(true)
            stopSelf()
            state = State.STOPPED
        } else {
            log("stopService: already stopped")
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun log(message: String) {
        message.let {
            Log.d(TAG, it)
            toast(it)
        }
    }

    companion object {
        private const val PORT = 8090
        private const val TAG = "ProxyService"
        const val SERVICE_COMMAND = "ProxyCommand"
    }
}