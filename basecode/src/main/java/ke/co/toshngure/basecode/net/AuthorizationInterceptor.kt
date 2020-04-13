package ke.co.toshngure.basecode.net

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

class AuthorizationInterceptor(
    private val clientInstance: OkHttpClient?,
    private val callback: NetworkUtil.Callback
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        // Get the request from the chain.
        val original = chain.request()

        val request = original.newBuilder()
            .url(original.url())
            .method(original.method(), original.body())
            .header("Authorization", "Bearer ${callback.getAuthToken()}")

        val response = chain.proceed(request.build())


        if (response.code() == 401 || response.code() == 403) {
            callback.onAuthError(response.code())
            clientInstance?.dispatcher()?.cancelAll()
        }

        return response
    }
}