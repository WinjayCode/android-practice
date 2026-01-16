package com.winjay.practice.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.winjay.practice.utils.LogUtil

class AlarmReceiver: BroadcastReceiver() {
    private val TAG = "AlarmReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        LogUtil.d(TAG, "alarm is dispatched.")
    }
}