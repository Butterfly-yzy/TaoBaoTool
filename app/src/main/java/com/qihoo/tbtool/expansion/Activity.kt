package com.mm.red.expansion

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*

import java.math.BigDecimal

fun Activity.showHintDialog(tile: String, msg: String, block: () -> Unit) {

    AlertDialog
            .Builder(this)
            .setTitle(tile)
            .setMessage(msg)
            .setPositiveButton("确定") { dialogInterface: DialogInterface, i: Int ->
                block()
            }
            .setNegativeButton("取消", null)
            .setCancelable(false)
            .show()

}
