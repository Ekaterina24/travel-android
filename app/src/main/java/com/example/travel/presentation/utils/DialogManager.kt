package com.example.travel.presentation.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

object DialogManager {
    fun showYesNoDialog(context: Context, titleId: String, msgId: String, OnYes: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        dialog.setTitle(titleId)
        dialog.setMessage(msgId)
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", OnYes)
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No") {
                _,_ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    interface Listener {
        fun onClick()
    }
}