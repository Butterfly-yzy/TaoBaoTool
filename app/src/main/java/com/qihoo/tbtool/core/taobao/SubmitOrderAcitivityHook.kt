package com.qihoo.tbtool.core.taobao

import android.app.Activity
import android.app.Dialog
import android.os.Build
import androidx.annotation.RequiresApi
import com.qihoo.tbtool.core.taobao.Core.submitOrder
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * 提交订单
 */
object SubmitOrderAcitivityHook {
    fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod(Activity::class.java, "onResume", object : XC_MethodHook() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                val activity: Activity = param.thisObject as Activity
                // 获取 Activity 的全路径昵称
                val simpleName = activity.javaClass.name
                // 判断注入了抢购悬浮窗
                if (simpleName == "com.taobao.android.purchase.TBPurchaseActivity") {
                    submitOrder(activity)
                }
            }
        })
    }
}