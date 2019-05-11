package ke.co.toshngure.basecode.extensions

import kotlinx.coroutines.*

fun executeAsync(action: () -> Unit) {

    GlobalScope.launch(Dispatchers.IO) {
        action.invoke()
    }
}

fun <Result> executeAsync(action: () -> Result, onCompletion: (result: Result) -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {

        val job = async(Dispatchers.IO) { action.invoke() }

        val result = job.await()

        onCompletion.invoke(result)

    }
}
