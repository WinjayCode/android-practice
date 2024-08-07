package com.winjay.practice.media.projection

import kotlinx.coroutines.*

private val job = Job()
private val scope = CoroutineScope(job)

fun scopeIo(block: suspend CoroutineScope.() -> Unit) =
        scope.launch(Dispatchers.IO) { block(this) }

fun scopeMain(block: suspend CoroutineScope.() -> Unit) =
        scope.launch(Dispatchers.Main) { block(this) }

suspend fun <T> withIo(block: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.IO) { block(this) }

fun CoroutineScope.scopeIo(block: suspend CoroutineScope.() -> Unit) =
        scope.launch(Dispatchers.IO) { block(this) }

fun CoroutineScope.scopeDe(block: suspend CoroutineScope.() -> Unit) =
        scope.launch(Dispatchers.Default) { block(this) }

fun CoroutineScope.scopeMain(block: suspend CoroutineScope.() -> Unit) =
        scope.launch(Dispatchers.Main) { block(this) }

suspend fun <T> withMain(block: suspend CoroutineScope.() -> T) =
        withContext(Dispatchers.Main) { block(this) }

//释放
fun releaseScope() {
    job.cancel()
}
