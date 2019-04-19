package com.udstu.fraxinus.web.util

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.fetch.RequestInit
import kotlin.browser.localStorage
import kotlin.browser.window
import kotlin.coroutines.*
import kotlin.js.Promise
import kotlin.js.json
import kotlin.reflect.KClass
import kotlinx.serialization.serializer

suspend fun <T> Promise<T>.await() = suspendCoroutine<T> { cont ->
    then { value ->
        cont.resume(value)
    }.catch { exception ->
        cont.resumeWithException(exception)
    }
}

fun <T> async(block: suspend () -> T): Promise<T> = Promise { resolve, reject ->
    block.startCoroutine(object : Continuation<T> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<T>) {
            if (result.isSuccess) {
                result.onSuccess { value ->
                    resolve(value)
                }
            } else {
                result.onFailure { exception ->
                    reject(exception)
                }
            }
        }
    })
}

@UnstableDefault
@ImplicitReflectionSerializer
object Requests {
    private fun prepareRequest(method: String, body: dynamic): RequestInit {
        val headers = json("Accept" to "application/json")

        if (localStorage["config"] != null) {
            headers["Authorization"] = localStorage["config"]
        }

        return object : RequestInit {
            override var method: String? = method
            override var body: dynamic = body
            override var headers: dynamic = headers
        }
    }

    private suspend fun <T : Any> execute(method: String, url: String, params: dynamic, body: dynamic, clz: KClass<T>): T {
        val query = (params?.getOwnPropertyNames()?.map { name ->
            "$name=${params[name]}"
        } as List<String>?)?.joinToString("&")

        val response = if (query == null) {
            window.fetch(url, prepareRequest(method, body))
        } else {
            window.fetch("$url?$query", prepareRequest(method, body))
        }.await()

        val serial = clz.serializer()

        return Json.parse(serial, response.json().await().toString())
    }

    suspend fun <T : Any> get(url: String, params: dynamic, clz: KClass<T>): T {
        return execute("GET", url, params, null, clz)
    }

    suspend fun <T : Any> post(url: String, body: dynamic, clz: KClass<T>): T {
        return execute("POST", url, null, body, clz)
    }
}

