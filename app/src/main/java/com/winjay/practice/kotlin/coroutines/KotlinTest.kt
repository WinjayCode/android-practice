package com.winjay.practice.kotlin.coroutines

import kotlinx.coroutines.*

/**
 * 协程是轻量级的线程
 * ⾮阻塞的 delay(……) 与 阻塞的 Thread.sleep(……)
 * runBlocking{} 主线程会⼀直阻塞直到 runBlocking 内部的协程执⾏完毕。
 *
 * @author Winjay
 * @date 2022/02/16
 */

/*fun main() {
    GlobalScope.launch { // 在后台启动⼀个新的协程并继续
        delay(1000L)
        println("World!")
    }
    println("Hello,") // 主线程中的代码会⽴即执⾏
    runBlocking { // 但是这个表达式阻塞了主线程
        delay(2000L) // ……我们延迟 2 秒来保证 JVM 的存活
    }
}*/


// 这⾥的 runBlocking<Unit> { …… } 作为⽤来启动顶层主协程的适配器。我们显式指定了其返回类型 Unit，因为在 Kotlin 中 main 函数必须返回 Unit 类型。
/*fun main() = runBlocking<Unit> { // 开始执⾏主协程
    GlobalScope.launch { // 在后台启动⼀个新的协程并继续
        delay(1000L)
        println("World!")
    }
    println("Hello,") // 主协程在这⾥会⽴即执⾏
    delay(2000L) // 延迟 2 秒来保证 JVM 存活
}*/


// 是主协程与后台作业的持续时间没有任何关系了
/*fun main() = runBlocking<Unit> {
    val job = GlobalScope.launch { // 启动⼀个新协程并保持对这个作业的引⽤
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    job.join() // 等待直到⼦协程执⾏结束
}*/


// 结构化并发
/*fun main() = runBlocking { // this: CoroutineScope
    launch { // 在 runBlocking 作⽤域中启动⼀个新协程
        delay(1000L)
        println("World!")
    }
    println("Hello,")
}*/


// 作⽤域构建器（它会创建⼀个协程作⽤域并且在所有已启动⼦协程执⾏完毕之前不会结束。runBlocking 与 coroutineScope 的主要区别在于后者在等待所有⼦协程执⾏完毕时不会阻塞当前线程。）
/*fun main() = runBlocking { // this: CoroutineScope
    launch {
        delay(200L)
        println("Task from runBlocking")
    }
    coroutineScope { // 创建⼀个协程作⽤域
        launch {
            delay(500L)
            println("Task from nested launch")
        }
        delay(100L)
        println("Task from coroutine scope") // 这⼀⾏会在内嵌 launch 之前输出
    }
    println("Coroutine scope is over") // 这⼀⾏在内嵌 launch 执⾏完毕后才输出
}*/
/*
输出结果：
Task from coroutine scope
Task from runBlocking
Task from nested launch
Coroutine scope is over
*/


// 提取函数重构
/*fun main() = runBlocking {
    launch { doWorld() }
    println("Hello,")
}

// 这是你的第⼀个挂起函数
suspend fun doWorld() {
    delay(1000L)
    println("World!")
}*/


// 协程很轻量
/*
fun main() = runBlocking {
    repeat(100_000) { // 启动⼤量的协程
        launch {
            delay(1000L)
            print(".")
        }
    }
}*/


// 全局协程像守护线程
/*fun main() = runBlocking<Unit> {
    // 在 GlobalScope 中启动的活动协程并不会使进程保活
    GlobalScope.launch {
        repeat(10) { i ->
            println("I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L) // 在延迟后退出
}*/


// 取消协程的执⾏
fun main() = runBlocking<Unit> {
    val job = launch {
        repeat(1000) { i ->
            println("job: I'm sleeping $i ...")
            delay(500L)
        }
    }
    delay(1300L) // 延迟⼀段时间
    println("main: I'm tired of waiting!")
    job.cancel() // 取消该作业
    job.join() // 等待作业执⾏结束
//    job.cancelAndJoin() // 合并了cancel()和join()的调用
    println("main: Now I can quit.")
}


