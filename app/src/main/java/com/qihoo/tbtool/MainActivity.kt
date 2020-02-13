package com.qihoo.tbtool

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mm.red.expansion.setOnceClick
import com.mm.red.expansion.showHintDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab.setOnceClick {
            openWebView("https://github.com/makeloveandroid/TaoBaoTool")
        }

        liability.setOnceClick {
            showHintDialog(
                "免责声明",
                "本软件仅供学习使用，完全模拟人工操作，抢购速度取决于你手机的性能与网络，不涉及任何第三方软件接口，本软件已开放源代码，无病毒、不收集用户隐私信息，禁止使用本软件参与非法活动。一切因使用造成的任何后果概不负责，亦不承担任何法律责任!"
            ) {

            }
        }

    }


    private fun openWebView(url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val content_url = Uri.parse(url)
        intent.data = content_url
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}



