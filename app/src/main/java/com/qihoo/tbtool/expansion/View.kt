package com.mm.red.expansion

import android.util.TypedValue
import android.view.View
import com.qihoo.tbtool.expansion.mainScope
import kotlinx.coroutines.channels.actor


// 使用扩展函数
fun View.dp2f(dp: Float): Float {
    // 引用View的context
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
}

// 转换Int
fun View.dp2i(dp: Float): Int {
    return dp2f(dp).toInt()
}

/**
 * 定义View 只能点一次的扩展方法
 */
fun View.setOnceClick(block: (view: View) -> Unit) {
    /**
     * 定义 协程 的一费者模式
     */
    val eventActor = mainScope.actor<View> {
        // 这里注意，协程 channel 若没有数据，会处于 挂起 状态。直到有数过来才会执行
        for (view in channel) {
            block(view)
        }
    }
    setOnClickListener {
        /**
         * 发送输出,若消费者,没有消费等待数据,发送数据就会失败
         */
        eventActor.offer(it)
    }
}



