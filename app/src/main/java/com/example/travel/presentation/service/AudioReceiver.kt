package com.example.travel.presentation.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AudioReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val actionMusic = intent.getIntExtra("action_music", 0)

        val intent_ser = Intent(context, AudioService::class.java)
        intent_ser.putExtra("action_mucsic_backser",actionMusic)

        context.startService(intent_ser)
    }
}