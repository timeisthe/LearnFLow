package com.example.tolearnflowandstateflow.flow

class SimpleFlow {
    //观察者
    fun interface MyCollector<T>{
        suspend fun emit(value: T)
    }
    interface MyFlow<T>{
        suspend fun collect(collector: MyCollector<T>)
    }
    fun <T> myFlow(block: suspend MyCollector<T>.()-> Unit): MyFlow<T>{
        return object : MyFlow<T>{
            override suspend fun collect(collector: MyCollector<T>) {
                block(collector)
            }
        }
    }
    suspend inline fun <T> MyFlow<T>.collect(crossinline action: suspend (T)-> Unit){
        collect(MyCollector { action(it) })
    }
    fun <T, R> MyFlow<T>.map(transform: (T) -> R): MyFlow<R>{
        return myFlow {
            this@map.collect {
                emit(transform(it))
            }
        }
    }

    fun demoIntFlow(): MyFlow<Int> {
        return myFlow {
            emit(1)
            emit(2)
            emit(3)
        }
    }
}