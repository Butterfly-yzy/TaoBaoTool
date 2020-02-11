package com.qihoo.tbtool.core.taobao.event

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.qihoo.tbtool.core.taobao.Core
import com.qihoo.tbtool.expansion.l
import com.qihoo.tbtool.expansion.mainScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SubmitOrderEvent : BaseEvent() {
    fun execute(activity: Activity) = mainScope.launch {
        // 循环检测页面是否加载完毕
        withContext(Dispatchers.Default) {
            while (true) {
                val resId = activity.resources.getIdentifier(
                    "purchase_bottom_layout",
                    "id",
                    activity.packageName
                )
                val group = activity.findViewById<View>(resId) as ViewGroup
                if (group != null && group.childCount != 0) {
                    break
                }
            }
        }

        // 获取提交按钮
        val submitBtn = getSubmitBtn(activity) ?: return@launch

        l("找到提交按钮:$submitBtn  ${submitBtn.text}")
//        submitBtn.performClick()
    }


    private fun getSubmitBtn(activity: Activity): TextView? {
        val resId =
            activity.resources.getIdentifier("purchase_bottom_layout", "id", activity.packageName)
        val group = activity.findViewById<View>(resId) as ViewGroup

        return group?.let {
            return findSubmitBtn(it)
        }
    }

    /**
     * 递归查找 提交按钮
     */
    private fun findSubmitBtn(group: ViewGroup): TextView? {
        for (v in 0..group.childCount) {
            val child = group.getChildAt(v)
            if (child is ViewGroup) {
                val submitBtn = findSubmitBtn(child)
                if (submitBtn != null) {
                    return submitBtn
                }
            } else {
                if (child is TextView) {
                    if ("提交订单" == child.text) {
                        return child
                    }
                }
            }
        }
        return null
    }


}