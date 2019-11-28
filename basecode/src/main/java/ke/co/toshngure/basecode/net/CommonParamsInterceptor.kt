package ke.co.toshngure.basecode.net

import okhttp3.Interceptor
import okhttp3.Response

class CommonParamsInterceptor(private val callback: NetworkUtils.Callback) : Interceptor {



    override fun intercept(chain: Interceptor.Chain): Response {
        // Get the request from the chain.
        val original = chain.request()

        // Get url
        val url = original.url().newBuilder()
        // Get common params
        val commonParams = callback.getCommonParams()
        // Add common params to the url
        for (key in commonParams.keys) {
            url.addQueryParameter(key, commonParams[key].toString())
        }

        val request = original.newBuilder()
            .url(url.build())
            .method(original.method(), original.body())
            .header("Accept", "application/json")

        return chain.proceed(request.build())
    }
}