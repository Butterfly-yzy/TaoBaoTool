package com.qihoo.tbtool.expansion

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class MyContextScope(context: CoroutineContext) : CoroutineScope {
    override val coroutineContext: CoroutineContext = context
}

val mainScope by lazy {
    createMyScope()
}

fun createMyScope(): MyContextScope {
    val dispatcherFactoryClzz = Class.forName("kotlinx.coroutines.android.AndroidDispatcherFactory")
    val factoryObj = dispatcherFactoryClzz.newInstance()
    val method = dispatcherFactoryClzz.getMethod("createDispatcher", List::class.java)
    val invoke = method.invoke(factoryObj, mutableListOf<Any>()) as CoroutineContext
    val myContextScope = MyContextScope(SupervisorJob() + invoke)
    return myContextScope
}