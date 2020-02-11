package com.qihoo.tbtool.core.taobao

import android.app.Dialog
import android.util.Log
import com.qihoo.tbtool.expansion.l
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers.findAndHookMethod
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * 订单内容 Dialog Hook
 */
object OrderDialogHook {
    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        findAndHookMethod(Dialog::class.java.name, lpparam.classLoader, "show", object :
            XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam) {
                super.beforeHookedMethod(param)
            }

            @Throws(Throwable::class)
            override fun afterHookedMethod(param: XC_MethodHook.MethodHookParam) {
                super.afterHookedMethod(param)
                val dialog = param.thisObject as Dialog
                val activity = dialog.ownerActivity ?: return
                if (activity.javaClass.name == "com.taobao.android.detail.wrapper.activity.DetailActivity") {
                    val isKill =
                        activity.intent.getBooleanExtra(TbDetailActivityHook.IS_KILL, false)
                    if (isKill) {
                        // 是秒杀Activity,进行订单选择
                        Core.orderChoose(dialog)
                    }
                }
            }
        })
    }
}