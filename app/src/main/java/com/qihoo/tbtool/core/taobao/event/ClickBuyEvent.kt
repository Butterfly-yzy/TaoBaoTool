package com.qihoo.tbtool.core.taobao.event

import android.app.Activity
import android.content.Intent
import android.view.ViewGroup
import android.widget.TextView
import com.mm.red.expansion.showHintDialog
import com.qihoo.tbtool.core.taobao.Core
import com.qihoo.tbtool.expansion.l
import com.qihoo.tbtool.expansion.mainScope
import kotlinx.coroutines.launch

/**
 * 点击立即购买按钮
 */
object ClickBuyEvent : BaseEvent() {

    fun execute(activity: Activity) = mainScope.launch {
        try {
            // 循环判断界面是否加载完毕
            checkLoadCompleteById(
                activity,
                activity.window,
                "ll_bottom_bar_content"
            )

            // 判断是否已经抢完了
            val hintText = judgeSoldThe(activity)

            if (hintText.isBlank()) {
                // 未出售光,点击购买按钮
                clickBuyBtn(activity)
            } else {
                activity.showHintDialog("提示", "检测到物品:$hintText") {}
            }


        } catch (e: Exception) {
            // 出错重新抢购
            Core.startGo(activity.applicationContext, activity.intent.clone() as Intent)
            activity.finish()
        }
    }


    /**
     * 判断物品是否已经寿光
     */
    private fun judgeSoldThe(activity: Activity): String {
        try {
            val resId = activity.resources.getIdentifier(
                "tv_hint",
                "id",
                activity.packageName
            )

            val tvHint: TextView = activity.findViewById(resId) ?: return ""
            l("tvHint:$tvHint  ${tvHint.text}")
            return tvHint.text.toString()
        } catch (e: Exception) {
        }
        return ""
    }


    private fun clickBuyBtn(activity: Activity) {
        l("开始执行购买")
        // 进入到这里可以确认界面加载完毕了
        val buy = getBuyButton(activity)
        if (buy != null) {
            l("买: " + buy.text + "  " + buy.isEnabled)
            // 获取到购买按钮,判断是否已经可以开始抢购了
            if (buy.isEnabled) {
                buy.performClick()
            } else {
                // 还没有开始抢购,重新检测
                Core.startGo(activity.applicationContext, activity.intent.clone() as Intent)
                activity.finish()
            }
        } else {
            // 获取按钮失败,重新开始
            Core.startGo(activity.applicationContext, activity.intent.clone() as Intent)
            activity.finish()
        }
    }


    /**
     * 根据布局层级
     * 获取立即购买的 Button 按钮
     */
    private fun getBuyButton(activity: Activity): TextView? {
        val resId = activity.resources.getIdentifier(
            "ll_bottom_bar_content",
            "id",
            activity.packageName
        )

        val bottomBarContent: ViewGroup? = activity.findViewById(resId)

        val group2: ViewGroup? = bottomBarContent?.getChildAt(4) as ViewGroup?

        val buyButton = group2?.getChildAt(0) as TextView?

        return buyButton
    }

}