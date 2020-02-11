package com.qihoo.tbtool.core.taobao

import kotlinx.coroutines.Job
import org.json.JSONObject

/**
 * 定时任务管理
 */

object JobManagers {
    val jobs = mutableMapOf<String, Job>()

    /**
     * 增加任务
     */
    fun addJob(id: String, job: Job) {
        jobs[id]?.cancel()
        jobs[id] = job
    }

    /**
     * 判断是否存在Job
     */
    fun haveJob(id: String): Boolean {
        return jobs[id] != null
    }

    /**
     * 删除Job
     */
    fun removeJob(id: String) {
        jobs[id]?.cancel()
        jobs.remove(id)
    }
}