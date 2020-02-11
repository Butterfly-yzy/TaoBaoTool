package com.qihoo.tbtool.core.taobao

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.mm.red.expansion.showHintDialog
import com.qihoo.tbtool.core.taobao.view.ChooseTime
import com.qihoo.tbtool.expansion.l
import com.qihoo.tbtool.expansion.mainScope
import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.math.log

object Core {
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

    /**
     * 开始秒杀
     */
    fun startGo(context: Context, intent: Intent) {
        val intent = intent.clone() as Intent
        intent.putExtra(TbDetailActivityHook.IS_KILL, true)
        intent.putExtra(TbDetailActivityHook.IS_KILL_GO, false)
        intent.putExtra(TbDetailActivityHook.IS_INJECT, false)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }


    /**
     * 检测是否可以抢购了
     */
    fun checkBuy(activity: Activity) = mainScope.launch {

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
            startGo(activity.applicationContext, activity.intent.clone() as Intent)
            activity.finish()
        }
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
                startGo(activity.applicationContext, activity.intent.clone() as Intent)
                activity.finish()
            }
        } else {
            // 获取按钮失败,重新开始
            startGo(activity.applicationContext, activity.intent.clone() as Intent)
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


    /**
     * 选择订单详情
     */
    fun orderChoose(dialog: Dialog) = mainScope.launch {
        // 子线程,循环判断界面是否加载完毕
        checkLoadCompleteById(dialog.context, dialog.window!!, "confirm")

        // 选择订单逻辑
        // todo 这里到时候增加选择订单逻辑


        // 点击确定按钮
        val resId =
            dialog.context.resources.getIdentifier("confirm", "id", dialog.context.packageName)
        val go = dialog.findViewById<View>(resId)
        if (go == null) {
            // 重新尝试抢购
            val activity = dialog.ownerActivity ?: return@launch
            startGo(activity.applicationContext, activity.intent.clone() as Intent)
            activity.finish()
        } else if (go.tag == null) {
            go.tag = "已经点击过"
            go.performClick()
        }
    }


    /**
     * 提交订单
     */
    fun submitOrder(activity: Activity) = mainScope.launch {
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
        submitBtn.performClick()
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


    /**
     * 开始定时抢购
     */
    fun statTimeGo(chooseTime: ChooseTime, activity: Activity) {
        val context = activity.applicationContext
        val intent = activity.intent.clone() as Intent
        val itemId = intent.getStringExtra("item_id") ?: ""


        val job = GlobalScope.launch {
            while (true) {
                val currentTimeMillis = System.currentTimeMillis()
                val time = chooseTime.time() - currentTimeMillis
                l("执行倒计时: $time")

                if (time <= 0) {
                    startGo(context, intent)
                    JobManagers.removeJob(itemId)
                    break
                }

                mainScope.launch {
                    Toast.makeText(activity, "剩余:" + time / 1000 + "秒", Toast.LENGTH_SHORT).show()
                }
                delay(1000)
            }
        }
        JobManagers.addJob(itemId, job)
    }

}