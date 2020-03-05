package ke.co.toshngure.basecode.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import ke.co.toshngure.basecode.logging.BeeLog
import retrofit2.Call
import retrofit2.Response
import java.util.*

/**
 * @author Anthony Ngure
 */
abstract class BaseNetworkWorker<R>(context: Context, params: WorkerParameters) : Worker(context, params) {


    override fun doWork(): Result {
        BeeLog.i(getTag(), "doWork ${Date()}")
        return try {
            getAPICall()?.let {
                val response = it.execute()
                return when {
                    response.isSuccessful && response.body() != null -> {
                        BeeLog.i(getTag(), response)
                        handleData(response.body()!!)
                    }
                    else -> {
                        onUnSuccessfulOrEmptyBody(response)
                    }
                }
            } ?: run {
                BeeLog.i(getTag(), "APICall is null, work failed!")
                Result.failure()
            }

        } catch (ex: Exception) {
            onException(ex)
        }
    }

    abstract fun handleData(data: R): Result

    abstract fun getAPICall(): Call<R>?

    abstract fun getTag():String


    private fun onException(ex: Exception): Result {
        BeeLog.e(ex)
        return Result.retry()
    }

    private fun onUnSuccessfulOrEmptyBody(response: Response<R>): Result {
        BeeLog.e(getTag(), response.message())
        return Result.retry()
    }




}