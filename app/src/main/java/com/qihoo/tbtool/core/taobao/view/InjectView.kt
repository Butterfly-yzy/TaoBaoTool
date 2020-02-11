package com.qihoo.tbtool.core.taobao.view

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.qihoo.tbtool.core.taobao.Core
import com.qihoo.tbtool.core.taobao.Core.statTimeGo
import com.qihoo.tbtool.core.taobao.JobManagers
import com.qihoo.tbtool.core.taobao.TbDetailActivityHook
import com.qihoo.tbtool.expansion.l
import kotlinx.coroutines.NonCancellable.start
import org.jetbrains.anko.*

val icon_start by lazy {
    val icon =
        "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"


    val decode = Base64.decode(icon, Base64.NO_WRAP)
    BitmapFactory.decodeByteArray(decode, 0, decode.size)
}
val icon_stop by lazy {
    val icon =
        "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"

    val decode = Base64.decode(icon, Base64.NO_WRAP)
    BitmapFactory.decodeByteArray(decode, 0, decode.size)
}

val icon_time by lazy {
    val icon =
        "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"

    val decode = Base64.decode(icon, Base64.NO_WRAP)
    BitmapFactory.decodeByteArray(decode, 0, decode.size)
}

class InjectView(val activity: Activity) {
    var timeIcon: ImageView? = null

    fun buidView() {
        val item_id = activity.intent.getStringExtra("item_id") ?: ""
        timeIcon?.apply {
            imageBitmap = if (JobManagers.haveJob(item_id)) {
                icon_stop
            } else {
                icon_time
            }
        }
    }

    fun getRootView(): View {
        val item_id = activity.intent.getStringExtra("item_id") ?: ""
        val rootView = activity.UI {
            verticalLayout {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.RIGHT or Gravity.CENTER
                    setMargins(0, 0, dip(5), 0)
                }

                imageView {
                    imageBitmap = icon_start
                    setOnClickListener {
                        startGo()
                    }
                }.lparams(dip(45), dip(45))

                timeIcon = imageView {
                    imageBitmap = if (JobManagers.haveJob(item_id)) {
                        icon_stop
                    } else {
                        icon_time
                    }

                    setOnClickListener {
                        if (JobManagers.haveJob(item_id)) {
                            JobManagers.removeJob(item_id)
                            imageBitmap = icon_time
                        } else {
                            timeGo {
                                statTimeGo(it, activity)
                                imageBitmap = icon_stop
                            }
                        }
                    }
                }.lparams(dip(45), dip(45)) {
                    setMargins(0, dip(15), 0, 0)
                }
            }

        }.view

        // 这里记录下 View 方便我下次查找 View
        activity.window.decorView.tag = this
        return rootView
    }

    /**
     * 定时开始秒杀
     */
    private fun timeGo(block: (ChooseTime) -> Unit) {
        TimeChooseDialog(activity, timeConfirmListener = block).show()
    }

    /**
     * 开始秒杀购物
     */
    private fun startGo() {
        Core.startGo(activity.applicationContext, activity.intent.clone() as Intent)
    }
}