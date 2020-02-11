package com.qihoo.tbtool.core

import com.qihoo.tbtool.core.taobao.OrderDialogHook
import com.qihoo.tbtool.core.taobao.SubmitOrderAcitivityHook
import com.qihoo.tbtool.core.taobao.TbDetailActivityHook
import com.qihoo.tbtool.expansion.l
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XpInit : IXposedHookLoadPackage {
    val PKG = "com.taobao.taobao"

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == PKG && lpparam.processName == PKG) {
            TbDetailActivityHook.hook(lpparam)
            OrderDialogHook.hook(lpparam)
            SubmitOrderAcitivityHook.hook(lpparam)
        }
    }
}