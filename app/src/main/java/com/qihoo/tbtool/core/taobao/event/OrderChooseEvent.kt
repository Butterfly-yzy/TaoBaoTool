package com.qihoo.tbtool.core.taobao.event

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.qihoo.tbtool.core.taobao.Core
import com.qihoo.tbtool.expansion.mainScope
import kotlinx.coroutines.launch
import java.util.ArrayList

object OrderChooseEvent : BaseEvent() {
    fun execute(dialog: Dialog) = mainScope.launch {
        // 子线程,循环判断界面是否加载完毕
        checkLoadCompleteById(dialog.context, dialog.window!!, "confirm")

        // 选择订单逻辑
        // todo 这里到时候增加选择订单逻辑
        selectOrderProperty(dialog)

        // 点击确定按钮
        val resId =
            dialog.context.resources.getIdentifier("confirm", "id", dialog.context.packageName)
        val go = dialog.findViewById<View>(resId)
        if (go == null) {
            // 重新尝试抢购
            val activity = dialog.ownerActivity ?: return@launch
            Core.startGo(activity.applicationContext, activity.intent.clone() as Intent)
            activity.finish()
        } else if (go.tag == null) {
            go.tag = "已经点击过"
            go.performClick()
        }

    }



    /**
     * 订单属性选择
     */
    private fun selectOrderProperty(dialog: Dialog) {

        try {
            val selectLayout = dialog.context.resources.getIdentifier(
                "sku_native_view_layout",
                "id",
                dialog.context.packageName
            )
            val selectLayoutView = dialog.findViewById<View>(selectLayout) as LinearLayout

            // 循环遍历,选择订单的属性
            for (i in 0 until selectLayoutView.childCount) {
                val views = ArrayList<View>()

                val group = selectLayoutView.getChildAt(i) as ViewGroup
                // 找到所有的选择条件
                findPropValueView(group, views)
                // todo 遗留问题,默认订单属性选择最后一个,有没有办法预设呢?
                if (views.size > 0) {

                    for (i1 in views.indices.reversed()) {
                        val view = views[i1]
                        Log.d("wyz", "选择: " + view.contentDescription)
                        // 判断能不能选
                        if (view.contentDescription.toString().contains("不可选择")) {
                            continue
                        }

                        if (!view.isSelected) {
                            view.performClick()
                            break
                        }
                    }


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun findPropValueView(view: View, views: ArrayList<View>) {
        if (view is ViewGroup) {
            if (view.toString().contains("PropValueView")) {
                views.add(view)
                return
            }

            for (i in 0 until view.childCount) {
                val childAt = view.getChildAt(i)
                findPropValueView(childAt, views)
            }

        }

    }
}