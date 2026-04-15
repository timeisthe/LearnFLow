package com.example.tolearnflowandstateflow.flow

import kotlinx.coroutines.channels.Channel
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.forEach

class SimpleSharedFlow<T> {
    // 1. 订阅者列表：这里存储每个订阅者的“信箱”（Channel）
    // 使用 CopyOnWriteArrayList 保证遍历时的线程安全
    private val subscribers = CopyOnWriteArrayList<Channel<T>>()

    // 2. 发送数据
    suspend fun emit(value: T) {
        // 广播给所有订阅者
        subscribers.forEach { channel ->
            // trySend 是非阻塞的，如果信箱满了就丢弃或阻塞（取决于配置）
            channel.send(value)
        }
    }

    // 3. 收集数据
    suspend fun collect(action: suspend (T) -> Unit) {
        // 每个 collect 的协程都拥有自己独立的信箱（Channel）
        val channel = Channel<T>(capacity = 10)
        subscribers.add(channel)

        try {
            // 4. 持续监听信箱里的新消息
            for (value in channel) {
                action(value)
            }
            // 因为热流通常是永不结束的，所以这里会一直循环
            throw IllegalStateException("Should not reach here")
        } finally {
            // 5. 协程取消时，务必移除订阅者，防止内存泄漏
            subscribers.remove(channel)
            channel.close()
        }
    }
}