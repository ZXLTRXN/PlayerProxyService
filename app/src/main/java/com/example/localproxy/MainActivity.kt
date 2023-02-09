package com.example.localproxy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.localproxy.service.Command
import com.example.localproxy.service.ServiceCommander
import com.example.localproxy.service.ServiceCommanderImpl

class MainActivity : AppCompatActivity(), ServiceCommander by ServiceCommanderImpl() {
    lateinit var startBtn: Button
    lateinit var stopBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startBtn = findViewById(R.id.start_btn)
        stopBtn = findViewById(R.id.stop_btn)

        startBtn.setOnClickListener {
            sendCommandToProxyService(this, Command.Start)
        }

        stopBtn.setOnClickListener {
            sendCommandToProxyService(this, Command.Stop)
        }
    }
}