package com.qihoo.tbtool.core.taobao.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Gravity
import android.app.Dialog
import android.view.WindowManager
import android.widget.LinearLayout
import com.mm.red.expansion.fillZero
import com.qihoo.tbtool.R
import com.qihoo.tbtool.core.taobao.view.wheelview.adapter.ArrayWheelAdapter
import com.qihoo.tbtool.core.taobao.view.wheelview.common.WheelConstants.WHEEL_TEXT_SIZE
import com.qihoo.tbtool.core.taobao.view.wheelview.widget.WheelView
import com.qihoo.tbtool.expansion.createMyScope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.jetbrains.anko.*
import java.util.*


/**
 * 时间选择器保存对象
 */
data class ChooseTime(var timeType: String, var hour: Int, var minutes: Int, var second: Int) {
    /**
     * 转换时间
     */
    fun time(): Long {
        val c = Calendar.getInstance()
        if (timeType == MORNING) {
            c.set(Calendar.HOUR_OF_DAY, hour)
        } else {
            if (hour == 12) {
                // 12点就是0点
                c.set(Calendar.HOUR_OF_DAY, 0)
            } else {
                c.set(Calendar.HOUR_OF_DAY, hour + 12)
            }
        }
        c.set(Calendar.MINUTE, minutes)
        c.set(Calendar.SECOND, second)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    override fun toString(): String {
        return "ChooseTime(timeType='$timeType', hour=$hour, minutes=$minutes, second=$second)"
    }

}

val MORNING = "上午"
val AFTERNOON = "下午"

class TimeChooseDialog(
    context: Context
    ,
    val default: ChooseTime = ChooseTime(MORNING, 6, 30, 0),
    val timeConfirmListener: (ChooseTime) -> Unit
) : Dialog(context),
    CoroutineScope by createMyScope() {
    private companion object {


        val DAY_TYPE = listOf(MORNING, AFTERNOON)

        val HOURS = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")

        val MINUTES = mutableListOf<String>().apply {
            repeat(60) {
                add(it.fillZero())
            }
        }
    }


    init {
        setContentView(buildView())
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(false)


        window!!.getDecorView().setPadding(0, 0, 0, 0)
        val lp = window!!.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.BOTTOM
        window!!.attributes = lp

        initView()
    }

    /**
     * 早上还是下午
     */
    lateinit var wvDayType: WheelView<String>

    /**
     * 时辰
     */
    lateinit var wvHour: WheelView<String>
    /**
     * 分钟
     */
    lateinit var wvMinutes: WheelView<String>

    /**
     * 秒
     */
    lateinit var wvSecond: WheelView<String>


    private fun buildView(): View {
        return context.UI {
            verticalLayout {
                linearLayout {
                    backgroundColor = Color.WHITE
                    orientation = LinearLayout.HORIZONTAL

                    textView("取消") {
                        textColor = Color.parseColor("#FFFF3B30")
                        textSize = 16f
                        padding = dip(10)
                        setOnClickListener {
                            dismiss()
                        }
                    }.lparams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )



                    textView("设置定时时间") {
                        gravity = Gravity.CENTER
                        textColor = Color.parseColor("#FF333333")
                        textSize = 16f

                    }.lparams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    ) {
                        weight = 1.0f
                    }



                    textView("确定") {
                        gravity = Gravity.CENTER
                        textColor = Color.parseColor("#FF35C759")
                        textSize = 16f
                        padding = dip(10)

                        setOnClickListener {
                            confirm()
                        }
                    }.lparams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )


                }.lparams(LinearLayout.LayoutParams.MATCH_PARENT, dip(44))


                linearLayout {
                    orientation = LinearLayout.HORIZONTAL

                    // 上午或下午
                    wvDayType = getWheelView(2.0f)
                    defaultWv(wvDayType, false, DAY_TYPE)
                    addView(wvDayType)

                    // 时
                    wvHour = getWheelView(1.0f)
                    defaultWv(wvHour, true, HOURS)
                    addView(wvHour)

                    // 分
                    wvMinutes = getWheelView(1.0f)

                    defaultWv(wvMinutes, true, MINUTES)
                    addView(wvMinutes)

                    // 秒
                    wvSecond = getWheelView(1.0f)
                    defaultWv(wvSecond, true, MINUTES)
                    addView(wvSecond)


                }.lparams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            }


        }.view

    }

    private fun <T> getWheelView(weightN: Float): WheelView<T> {
        return WheelView<T>(context).apply {
            layoutParams =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                    weight = weightN
                }
        }

    }


    /**
     * 初始化 wv
     */
    fun initView() {


    }

    fun <T> defaultWv(wv: WheelView<T>, loop: Boolean, data: List<T>) {
        wv.setWheelAdapter(ArrayWheelAdapter<T>(context))
        wv.skin = WheelView.Skin.Holo
        //
        wv.style = WheelView.WheelViewStyle().apply {
            backgroundColor = Color.parseColor("#F2F2F2")
            selectedTextColor = Color.parseColor("#FF333333")
            selectedTextSize = WHEEL_TEXT_SIZE + 4
            holoBorderColor = Color.parseColor("#DADFE0")

        }

        wv.setExtraText("", Color.parseColor("#0288ce"), 40, 70, true)
        wv.setWheelSize(5)
        wv.setLoop(loop)

        wv.setWheelData(data)
        wv.selection = data.size / 2
        wv.visibility = View.VISIBLE


    }


    private fun confirm() {
        default.timeType= wvDayType.selectionItem
        default.hour = wvHour.selectionItem.toInt()
        default.minutes = wvMinutes.selectionItem.toInt()
        default.second = wvSecond.selectionItem.toInt()
        timeConfirmListener(default)
        dismiss()
    }


    override fun dismiss() {
        super.dismiss()
        this.coroutineContext.cancel()
    }


}

