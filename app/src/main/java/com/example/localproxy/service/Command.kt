package com.example.localproxy.service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

interface Command: Parcelable {
    @Parcelize
    object Start: Command
    @Parcelize
    object Stop: Command
}