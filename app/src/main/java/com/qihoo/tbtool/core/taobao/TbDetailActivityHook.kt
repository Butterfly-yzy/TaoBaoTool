package com.qihoo.tbtool.core.taobao

import android.app.Activity
import android.os.Build
import android.system.Os.kill
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.qihoo.tbtool.core.taobao.view.InjectView
import com.qihoo.tbtool.expansion.l
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage


object TbDetailActivityHook {
    /**
     * 是否是秒杀 Activity
     */
    const val IS_KILL = "IS_KILL"
    /**
     * 是否已经走过秒杀过逻辑
     */
    const val IS_KILL_GO = "IS_KILL_GO"

    /**
     * 判断是否注入过控制按钮
     */
    const val IS_INJECT = "IS_INJECT"

    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(Activity::class.java, "onResume", object : XC_MethodHook() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                val activity: Activity = param.thisObject as Activity
                // 获取 Activity 的全路径昵称
                val simpleName = activity.javaClass.name
                // 判断注入了抢购悬浮窗
                if (simpleName == "com.taobao.android.detail.wrapper.activity.DetailActivity") {
                    val isInject = activity.intent.getBooleanExtra(IS_INJECT, false)
                    val isKill = activity.intent.getBooleanExtra(IS_KILL, false)
                    val isKillGo = activity.intent.getBooleanExtra(IS_KILL_GO, false)

                    // 判断是否是秒杀界面
                    if (isKill) {
                        // 防止触发多次秒杀判断
                        if (!isKillGo) {
                            activity.intent.putExtra(IS_KILL_GO, true)
                            // 秒杀 Activity
                            killGo(activity)
                        }
                    } else if (!isInject) {
                        // 非秒杀 Activity
                        activity.intent.putExtra(IS_INJECT, true)
                        // 注入抢购按钮
                        injectView(activity)
                    } else if (isInject) {
                        // 注入过 View 刷新下状态
                        activity.window.decorView.tag?.let {
                            val injectView = it as InjectView
                            injectView.buidView()
                        }
                    }
                }
            }
        })

    }

    /**
     * 进入秒杀逻辑
     */
    private fun killGo(activity: Activity) {
        Core.checkBuy(activity)
    }

    /**
     * 注入抢购按钮
     */
    private fun injectView(activity: Activity) {
        val group = activity.findViewById<View>(android.R.id.content) as ViewGroup
        InjectView(activity).getRootView().let(group::addView)
    }
}