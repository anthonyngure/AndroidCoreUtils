package ke.co.toshngure.basecode.dataloading.sync

import android.content.Context
import android.text.TextUtils
import ke.co.toshngure.basecode.logging.BeeLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object SyncStateManager {

    fun sync(clazz: Class<*>, context: Context){
        val model = clazz.simpleName
        runBlocking {
            withContext(Dispatchers.Default) {
                Thread.sleep(20000)
                val ids = arrayListOf<Long>()
                for (i in 1..500) {
                    ids.add((i + 10000000000000000))
                }
                val data = TextUtils.join(",", ids)
                BeeLog.i(model, data)
                BeeLog.i(model, "Data Length = ${data.toByteArray().size}")

                val state = SyncStatesDatabase.getInstance(context).syncStates().findByModel(model)

                BeeLog.i(model, state?.toString())
            }
        }
    }
}
