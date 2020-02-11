package com.qihoo.tbtool.core.taobao.event

import android.content.Context
import android.view.View
import android.view.Window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseEvent{
    /**
     * 根据资源 Id  判断资源是否加载完毕
     */
    suspend fun checkLoadCompleteById(context: Context, window: Window, nameId: String) {
        withContext(Dispatchers.Default) {
            while (true) {
                // 获取立即购买布局的对应ID
                val resId = context.resources.getIdentifier(
                    nameId,
                    "id",
                    context.packageName
                )

                // 循环判断界面是否加载完毕
                val bottomBarContent: View? = window.findViewById(resId)
                // 若寻找到了  bottomBarContent 代表,界面加载完毕了
                if (bottomBarContent != null) {
                    break
                }
            }
        }
    }
}