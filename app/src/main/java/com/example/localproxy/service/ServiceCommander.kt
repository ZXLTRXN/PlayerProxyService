package com.example.localproxy.service

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.ContextCompat

interface ServiceCommander {
    fun sendCommandToProxyService(context: Context, command: Command)
}

class ServiceCommanderImpl() : ServiceCommander {
    override fun sendCommandToProxyService(context: Context, command: Command) {
        ContextCompat.startForegroundService(
            context,
            Intent(context, ProxyService::class.java)
                .putExtra(ProxyService.SERVICE_COMMAND, command as Parcelable)
        )
    }
}